import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth, dashboardPath } from '../auth'
import { useForm } from '../hooks'
import { Alert, Field } from '../components/ui'

export default function SignIn() {
  const { user, signIn } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const { values, set, setValues, errors, busy, formError, submit } = useForm({ email: '', password: '' })

  if (user) return <Navigate to={dashboardPath(user)} replace />

  const onSubmit = submit(async (v) => {
    const u = await signIn(v.email, v.password)
    navigate(location.state?.from || dashboardPath(u), { replace: true })
  })

  return (
    <div className="auth-page">
      <form className="card auth-card" onSubmit={onSubmit} noValidate>
        <h1>Welcome back</h1>
        <p className="muted">Sign in to book tests and see your results.</p>

        <Alert kind="info">{location.state?.message}</Alert>
        <Alert>{formError}</Alert>

        <Field label="Email" error={errors.email} id="email">
          <input id="email" className="input" type="email" autoComplete="email" value={values.email} onChange={set('email')} required />
        </Field>
        <Field label="Password" error={errors.password} id="password">
          <input id="password" className="input" type="password" autoComplete="current-password" value={values.password} onChange={set('password')} required />
        </Field>

        <button className="btn btn-primary btn-block" disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}</button>

        <div className="demo-fill">
          <span className="muted small">Demo accounts:</span>
          <button type="button" className="chip" onClick={() => setValues({ email: 'patient@healthcare.demo', password: 'Patient@12345' })}>Patient</button>
          <button type="button" className="chip" onClick={() => setValues({ email: 'admin@healthcare.demo', password: 'Admin@12345' })}>Admin</button>
        </div>

        <p className="muted small center">New here? <Link to="/signup">Create an account</Link></p>
      </form>
    </div>
  )
}
