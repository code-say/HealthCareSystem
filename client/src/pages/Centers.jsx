import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { useApi } from '../hooks'
import { formatPrice } from '../api'
import { Alert, Spinner } from '../components/ui'
import { useAuth } from '../auth'

export default function Centers() {
  const { data: centers, error, loading } = useApi('/api/centers')
  const { data: tests } = useApi('/api/tests')
  const [query, setQuery] = useState('')
  const { user } = useAuth()

  const byCenter = useMemo(() => {
    const map = {}
    tests?.forEach((t) => (map[t.diagnosticCenterId] ??= []).push(t))
    return map
  }, [tests])

  const q = query.trim().toLowerCase()
  const shown = (centers ?? []).filter(
    (c) =>
      !q ||
      c.name.toLowerCase().includes(q) ||
      c.address.toLowerCase().includes(q) ||
      byCenter[c.id]?.some((t) => t.testName.toLowerCase().includes(q))
  )

  return (
    <div className="container page">
      <div className="page-head">
        <div>
          <h1>Centers &amp; tests</h1>
          <p className="muted">Prices are per test. Book any combination at one center.</p>
        </div>
        <input
          className="input search"
          type="search"
          placeholder="Search a city, center or test"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          aria-label="Search centers and tests"
        />
      </div>

      <Alert>{error}</Alert>
      {loading && <Spinner />}

      <div className="stack">
        {shown.map((c) => (
          <article className="card" key={c.id}>
            <div className="center-head">
              <div>
                <h2>{c.name}</h2>
                <p className="muted">{c.address}</p>
                <p className="muted small">{c.contactNo} · {c.contactEmail}</p>
              </div>
              <Link className="btn btn-primary" to={user?.role === 'Patient' ? `/dashboard?book=${c.id}` : user ? '/admin' : '/signup'}>
                Book here
              </Link>
            </div>
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr><th>Test</th><th>Normal range</th><th className="num">Price</th></tr>
                </thead>
                <tbody>
                  {(byCenter[c.id] ?? []).map((t) => (
                    <tr key={t.id}>
                      <td>{t.testName}</td>
                      <td className="muted">{t.normalValue} {t.units}</td>
                      <td className="num">{formatPrice(t.testPrice)}</td>
                    </tr>
                  ))}
                  {!byCenter[c.id]?.length && <tr><td colSpan={3} className="muted">No tests listed yet.</td></tr>}
                </tbody>
              </table>
            </div>
          </article>
        ))}
        {!loading && centers && !shown.length && <p className="muted">No centers match “{query}”.</p>}
      </div>
    </div>
  )
}
