import { useEffect, useState } from 'react'
import { Link, NavLink, Outlet, useLocation } from 'react-router-dom'
import { useAuth, dashboardPath } from '../auth'
import { onSlowRequest, API_URL } from '../api'

export function Logo() {
  return (
    <Link to="/" className="logo" aria-label="HealthCare home">
      <span className="logo-mark" aria-hidden="true">
        <svg viewBox="0 0 24 24" width="18" height="18"><path fill="currentColor" d="M10 3h4v7h7v4h-7v7h-4v-7H3v-4h7z" /></svg>
      </span>
      HealthCare
    </Link>
  )
}

export default function Layout() {
  const { user, signOut } = useAuth()
  const [menuOpen, setMenuOpen] = useState(false)
  const [slow, setSlow] = useState(false)
  const location = useLocation()

  useEffect(() => setMenuOpen(false), [location.pathname])
  useEffect(() => onSlowRequest(setSlow), [])

  return (
    <div className="app">
      <header className="topbar">
        <div className="container topbar-inner">
          <Logo />
          <button className="menu-toggle" onClick={() => setMenuOpen((o) => !o)} aria-expanded={menuOpen} aria-label="Menu">
            <span /><span /><span />
          </button>
          <nav className={`nav ${menuOpen ? 'open' : ''}`} aria-label="Main">
            <NavLink to="/" end>Home</NavLink>
            <NavLink to="/centers">Centers &amp; tests</NavLink>
            {user ? (
              <>
                <NavLink to={dashboardPath(user)}>{user.role === 'Admin' ? 'Admin dashboard' : 'My dashboard'}</NavLink>
                <span className="nav-user" title={user.email}>{user.name}</span>
                <button className="btn btn-ghost btn-sm" onClick={signOut}>Sign out</button>
              </>
            ) : (
              <>
                <NavLink to="/signin">Sign in</NavLink>
                <Link to="/signup" className="btn btn-primary btn-sm">Create account</Link>
              </>
            )}
          </nav>
        </div>
      </header>

      {slow && (
        <div className="waking" role="status">
          <span className="spinner" aria-hidden="true" /> Waking up the server. The free hosting sleeps when idle, so the first request can take up to a minute.
        </div>
      )}

      <main>
        <Outlet />
      </main>

      <footer className="footer">
        <div className="container footer-inner">
          <p>
            <strong>HealthCare</strong> · a full-stack demo built with ASP.NET Core, EF Core and React by{' '}
            <a href="https://github.com/code-say" target="_blank" rel="noreferrer">Sayan Mandal</a>.
          </p>
          <p className="muted">
            <a href={`${API_URL}/scalar/v1`} target="_blank" rel="noreferrer">API reference</a>
            {' · '}
            <a href="https://github.com/code-say/HealthCareSystem" target="_blank" rel="noreferrer">Source on GitHub</a>
            {' · '}Demo data only. Not a real medical service.
          </p>
        </div>
      </footer>
    </div>
  )
}
