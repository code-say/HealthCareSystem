import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, loadSession, saveSession, setUnauthorizedHandler } from './api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [session, setSession] = useState(loadSession)
  const navigate = useNavigate()

  const signIn = useCallback(async (email, password) => {
    const res = await api('/api/auth/login', { method: 'POST', body: { email, password } })
    const s = { token: res.token, expiresAt: res.expiresAt, user: res.user }
    saveSession(s)
    setSession(s)
    return s.user
  }, [])

  const register = useCallback(async (form) => {
    const res = await api('/api/auth/register', { method: 'POST', body: form })
    const s = { token: res.token, expiresAt: res.expiresAt, user: res.user }
    saveSession(s)
    setSession(s)
    return s.user
  }, [])

  const signOut = useCallback(() => {
    saveSession(null)
    setSession(null)
    navigate('/')
  }, [navigate])

  // Token expired or revoked: sign out and ask to sign in again
  useEffect(() => {
    setUnauthorizedHandler(() => {
      saveSession(null)
      setSession(null)
      navigate('/signin', { state: { message: 'Your session expired. Please sign in again.' } })
    })
  }, [navigate])

  const value = useMemo(
    () => ({
      user: session?.user ?? null,
      isAdmin: session?.user?.role === 'Admin',
      signIn,
      register,
      signOut,
      updateName: (name) =>
        setSession((s) => {
          const next = { ...s, user: { ...s.user, name } }
          saveSession(next)
          return next
        }),
    }),
    [session, signIn, register, signOut]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)

export const dashboardPath = (user) => (user?.role === 'Admin' ? '/admin' : '/dashboard')
