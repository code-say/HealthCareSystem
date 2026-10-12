import { Link } from 'react-router-dom'
import { useAuth, dashboardPath } from '../auth'
import { useApi } from '../hooks'
import { formatPrice } from '../api'

const steps = [
  { n: '1', title: 'Pick a center', text: 'Browse diagnostic centers near you and the tests each one offers, with clear prices.' },
  { n: '2', title: 'Book your tests', text: 'Choose a date and one or more tests. Your request goes to the center for approval.' },
  { n: '3', title: 'Get your results', text: 'Track approval status and see your results with normal ranges as soon as they are recorded.' },
]

export default function Home() {
  const { user } = useAuth()
  const { data: centers } = useApi('/api/centers')
  const { data: tests } = useApi('/api/tests')

  const popular = tests
    ? Object.values(
        tests.reduce((acc, t) => {
          const cur = acc[t.testName]
          acc[t.testName] = cur ? { ...cur, from: Math.min(cur.from, t.testPrice) } : { name: t.testName, from: t.testPrice, units: t.units }
          return acc
        }, {})
      ).slice(0, 6)
    : []

  return (
    <>
      <section className="hero">
        <div className="container hero-inner">
          <div className="hero-copy">
            <p className="eyebrow">Diagnostic tests, booked online</p>
            <h1>Book lab tests in minutes. See results without the wait.</h1>
            <p className="lead">
              Choose a diagnostic center, pick your tests and a date, and follow your appointment from approval to
              results, all in one place.
            </p>
            <div className="hero-actions">
              {user ? (
                <Link to={dashboardPath(user)} className="btn btn-primary btn-lg">Go to my dashboard</Link>
              ) : (
                <>
                  <Link to="/signup" className="btn btn-primary btn-lg">Book a test</Link>
                  <Link to="/signin" className="btn btn-ghost btn-lg">I have an account</Link>
                </>
              )}
            </div>
            <div className="demo-box">
              <strong>Try the demo</strong>
              <span>Patient: <code>patient@healthcare.demo</code> / <code>Patient@12345</code></span>
              <span>Admin: <code>admin@healthcare.demo</code> / <code>Admin@12345</code></span>
            </div>
          </div>

          <div className="hero-card" aria-hidden="true">
            <div className="hc-head">
              <span className="hc-dot" /> Appointment #1024
              <span className="badge badge-approved">Approved</span>
            </div>
            <div className="hc-row"><span>Complete Blood Count</span><span className="badge badge-approved">Normal</span></div>
            <div className="hc-row"><span>Fasting Blood Sugar</span><span className="badge badge-rejected">High</span></div>
            <div className="hc-row"><span>Thyroid (TSH)</span><span className="badge badge-approved">Normal</span></div>
            <div className="hc-foot">CareFirst Diagnostics · Kolkata</div>
          </div>
        </div>
      </section>

      <section className="section">
        <div className="container">
          <h2 className="section-title">How it works</h2>
          <div className="steps">
            {steps.map((s) => (
              <div className="step" key={s.n}>
                <span className="step-n">{s.n}</span>
                <h3>{s.title}</h3>
                <p>{s.text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="section section-alt">
        <div className="container">
          <div className="section-head">
            <h2 className="section-title">Our diagnostic centers</h2>
            <Link to="/centers" className="link">See all tests →</Link>
          </div>
          <div className="grid-3">
            {(centers ?? [{}, {}, {}]).map((c, i) => (
              <article className={`card center-card ${c.id ? '' : 'skeleton'}`} key={c.id ?? i}>
                <h3>{c.name ?? ' '}</h3>
                <p className="muted">{c.address ?? ' '}</p>
                <p className="center-meta">
                  {c.id && <><span>{c.testCount} tests</span><span>{c.contactNo}</span></>}
                </p>
              </article>
            ))}
          </div>
        </div>
      </section>

      {popular.length > 0 && (
        <section className="section">
          <div className="container">
            <h2 className="section-title">Popular tests</h2>
            <div className="pill-grid">
              {popular.map((t) => (
                <div className="pill-card" key={t.name}>
                  <span>{t.name}</span>
                  <strong>from {formatPrice(t.from)}</strong>
                </div>
              ))}
            </div>
          </div>
        </section>
      )}
    </>
  )
}
