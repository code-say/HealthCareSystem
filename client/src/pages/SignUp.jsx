import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth, dashboardPath } from '../auth'
import { useForm } from '../hooks'
import { Alert, Field } from '../components/ui'

export default function SignUp() {
  const { user, register } = useAuth()
  const navigate = useNavigate()
  const { values, set, errors, busy, formError, submit } = useForm({
    name: '', email: '', password: '', phoneNo: '', age: '', gender: '',
  })

  if (user) return <Navigate to={dashboardPath(user)} replace />

  const onSubmit = submit(async (v) => {
    await register({ ...v, age: Number(v.age) || 0 })
    navigate('/dashboard', { replace: true })
  })

  return (
    <div className="auth-page">
      <form className="card auth-card wide" onSubmit={onSubmit} noValidate>
        <h1>Create your account</h1>
        <p className="muted">It takes a minute. You can book your first test straight after.</p>

        <Alert>{formError}</Alert>

        <Field label="Full name" error={errors.name} id="name">
          <input id="name" className="input" autoComplete="name" value={values.name} onChange={set('name')} required />
        </Field>
        <div className="form-row">
          <Field label="Email" error={errors.email} id="email">
            <input id="email" className="input" type="email" autoComplete="email" value={values.email} onChange={set('email')} required />
          </Field>
          <Field label="Phone" error={errors.phoneNo} id="phone">
            <input id="phone" className="input" type="tel" autoComplete="tel" placeholder="+91 98765 43210" value={values.phoneNo} onChange={set('phoneNo')} required />
          </Field>
        </div>
        <div className="form-row">
          <Field label="Age" error={errors.age} id="age">
            <input id="age" className="input" type="number" min="0" max="120" value={values.age} onChange={set('age')} required />
          </Field>
          <Field label="Gender" error={errors.gender} id="gender">
            <select id="gender" className="input" value={values.gender} onChange={set('gender')} required>
              <option value="">Choose…</option>
              <option>Female</option>
              <option>Male</option>
              <option>Other</option>
            </select>
          </Field>
        </div>
        <Field label="Password" error={errors.password} hint="At least 8 characters." id="password">
          <input id="password" className="input" type="password" autoComplete="new-password" value={values.password} onChange={set('password')} required />
        </Field>

        <button className="btn btn-primary btn-block" disabled={busy}>{busy ? 'Creating account…' : 'Create account'}</button>
        <p className="muted small center">Already registered? <Link to="/signin">Sign in</Link></p>
      </form>
    </div>
  )
}
