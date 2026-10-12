// Thin fetch wrapper for the ASP.NET Core API.
export const API_URL = (import.meta.env.VITE_API_URL || 'http://localhost:5080').replace(/\/$/, '')

const TOKEN_KEY = 'healthcare.auth'

export function loadSession() {
  try {
    const raw = localStorage.getItem(TOKEN_KEY)
    if (!raw) return null
    const session = JSON.parse(raw)
    if (!session?.token || new Date(session.expiresAt) <= new Date()) {
      localStorage.removeItem(TOKEN_KEY)
      return null
    }
    return session
  } catch {
    return null
  }
}

export function saveSession(session) {
  try {
    if (session) localStorage.setItem(TOKEN_KEY, JSON.stringify(session))
    else localStorage.removeItem(TOKEN_KEY)
  } catch {
    /* storage unavailable: session lasts for this tab only */
  }
}

export class ApiError extends Error {
  constructor(status, message, fieldErrors) {
    super(message)
    this.status = status
    this.fieldErrors = fieldErrors || {}
  }
}

let onUnauthorized = () => {}
export const setUnauthorizedHandler = (fn) => { onUnauthorized = fn }

// Free hosting sleeps when idle; let the UI say so if a request is slow.
let slowListeners = new Set()
export const onSlowRequest = (fn) => { slowListeners.add(fn); return () => slowListeners.delete(fn) }

export async function api(path, { method = 'GET', body, token } = {}) {
  const session = token ? { token } : loadSession()
  const headers = { Accept: 'application/json' }
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (session?.token) headers.Authorization = `Bearer ${session.token}`

  const slowTimer = setTimeout(() => slowListeners.forEach((fn) => fn(true)), 4000)
  let res
  try {
    res = await fetch(`${API_URL}${path}`, { method, headers, body: body === undefined ? undefined : JSON.stringify(body) })
  } catch {
    throw new ApiError(0, "Can't reach the server. It may be waking up; please try again in a few seconds.")
  } finally {
    clearTimeout(slowTimer)
    slowListeners.forEach((fn) => fn(false))
  }

  if (res.status === 204) return null
  const isJson = res.headers.get('content-type')?.includes('json')
  const data = isJson ? await res.json() : null

  if (!res.ok) {
    if (res.status === 401 && session?.token) onUnauthorized()
    const fieldErrors = data?.errors
      ? Object.fromEntries(Object.entries(data.errors).map(([k, v]) => [k.charAt(0).toLowerCase() + k.slice(1), v.join(' ')]))
      : undefined
    const message =
      data?.detail ||
      (fieldErrors && Object.values(fieldErrors)[0]) ||
      data?.title ||
      (res.status === 403 ? "You don't have access to that." : `Request failed (${res.status}).`)
    throw new ApiError(res.status, message, fieldErrors)
  }
  return data
}

export const formatPrice = (n) => `₹${Number(n).toLocaleString('en-IN', { maximumFractionDigits: 2 })}`
export const formatDate = (d) =>
  new Date(`${d}T00:00:00`).toLocaleDateString('en-IN', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' })
export const todayIso = () => {
  const d = new Date()
  return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 10)
}
