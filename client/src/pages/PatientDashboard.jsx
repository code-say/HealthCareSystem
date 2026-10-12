import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { api, formatDate, formatPrice, todayIso } from '../api'
import { useApi, useForm } from '../hooks'
import { useAuth } from '../auth'
import { Alert, ConditionBadge, Empty, Field, Spinner, StatusBadge, Tabs } from '../components/ui'

export default function PatientDashboard() {
  const { user } = useAuth()
  const [params, setParams] = useSearchParams()
  const [tab, setTab] = useState(params.get('book') ? 'book' : 'appointments')
  const [notice, setNotice] = useState(null)
  const appointments = useApi('/api/appointments')

  const upcoming = (appointments.data ?? []).filter((a) => a.status === 'Pending' || a.status === 'Approved')
  const results = (appointments.data ?? []).flatMap((a) => a.results.map((r) => ({ ...r, date: a.appointmentDate, center: a.centerName })))

  const changeTab = (t) => {
    setTab(t)
    if (t !== 'book' && params.get('book')) setParams({}, { replace: true })
  }

  return (
    <div className="container page">
      <div className="page-head">
        <div>
          <p className="eyebrow">Patient dashboard</p>
          <h1>Hello, {user.name?.split(' ')[0]}</h1>
        </div>
        <button className="btn btn-primary" onClick={() => changeTab('book')}>Book a test</button>
      </div>

      <div className="stats">
        <div className="stat"><span>Upcoming</span><strong>{appointments.data ? upcoming.length : '–'}</strong></div>
        <div className="stat"><span>All appointments</span><strong>{appointments.data?.length ?? '–'}</strong></div>
        <div className="stat"><span>Results</span><strong>{appointments.data ? results.length : '–'}</strong></div>
      </div>

      <Alert kind="success" onClose={() => setNotice(null)}>{notice}</Alert>

      <Tabs
        active={tab}
        onChange={changeTab}
        tabs={[
          { key: 'appointments', label: 'Appointments', count: appointments.data?.length },
          { key: 'book', label: 'Book' },
          { key: 'results', label: 'Results', count: results.length || undefined },
          { key: 'profile', label: 'Profile' },
        ]}
      />

      {tab === 'appointments' && <AppointmentsList state={appointments} onBook={() => changeTab('book')} />}
      {tab === 'book' && (
        <BookForm
          initialCenter={params.get('book')}
          onBooked={async () => {
            setNotice('Appointment requested. You’ll see it as Approved once the center confirms it.')
            await appointments.reload()
            changeTab('appointments')
          }}
        />
      )}
      {tab === 'results' && <ResultsTable results={results} loading={appointments.loading} />}
      {tab === 'profile' && <Profile onSaved={() => setNotice('Profile updated.')} />}
    </div>
  )
}

function AppointmentsList({ state, onBook }) {
  const { data, error, loading, reload } = state
  const [busyId, setBusyId] = useState(null)
  const [err, setErr] = useState(null)

  const cancel = async (id) => {
    if (!window.confirm('Cancel this appointment?')) return
    setBusyId(id)
    setErr(null)
    try {
      await api(`/api/appointments/${id}/cancel`, { method: 'POST' })
      await reload()
    } catch (e) {
      setErr(e.message)
    } finally {
      setBusyId(null)
    }
  }

  if (loading && !data) return <Spinner />
  if (error) return <Alert>{error}</Alert>
  if (!data?.length)
    return <Empty title="No appointments yet" action={<button className="btn btn-primary" onClick={onBook}>Book your first test</button>}>Book a test at any of our centers.</Empty>

  return (
    <div className="stack">
      <Alert>{err}</Alert>
      {data.map((a) => (
        <article className="card appt" key={a.id}>
          <div className="appt-head">
            <div>
              <h3>{a.centerName}</h3>
              <p className="muted">{formatDate(a.appointmentDate)} · #{a.id}</p>
            </div>
            <StatusBadge status={a.status} />
          </div>
          <ul className="test-list">
            {a.tests.map((t) => {
              const r = a.results.find((x) => x.diagnosticTestId === t.id)
              return (
                <li key={t.id}>
                  <span>{t.testName}</span>
                  {r ? (
                    <span className="result">
                      <strong>{r.testReading} {r.units}</strong>
                      <span className="muted small">normal {r.normalValue}</span>
                      <ConditionBadge condition={r.condition} />
                    </span>
                  ) : (
                    <span className="muted">{formatPrice(t.testPrice)}</span>
                  )}
                </li>
              )
            })}
          </ul>
          <div className="appt-foot">
            <span>Total <strong>{formatPrice(a.totalPrice)}</strong></span>
            {(a.status === 'Pending' || a.status === 'Approved') && a.results.length === 0 && (
              <button className="btn btn-ghost btn-sm" onClick={() => cancel(a.id)} disabled={busyId === a.id}>
                {busyId === a.id ? 'Cancelling…' : 'Cancel'}
              </button>
            )}
          </div>
        </article>
      ))}
    </div>
  )
}

function BookForm({ initialCenter, onBooked }) {
  const { data: centers } = useApi('/api/centers')
  const [centerId, setCenterId] = useState(initialCenter || '')
  const { data: tests, loading: testsLoading } = useApi(centerId ? `/api/tests?centerId=${centerId}` : null, { skip: !centerId })
  const { values, set, setValues, errors, busy, formError, submit } = useForm({ date: '', testIds: [] })

  useEffect(() => setValues((v) => ({ ...v, testIds: [] })), [centerId, setValues])

  const toggle = (id) =>
    setValues((v) => ({ ...v, testIds: v.testIds.includes(id) ? v.testIds.filter((x) => x !== id) : [...v.testIds, id] }))

  const total = useMemo(
    () => (tests ?? []).filter((t) => values.testIds.includes(t.id)).reduce((s, t) => s + t.testPrice, 0),
    [tests, values.testIds]
  )

  const onSubmit = submit(async (v) => {
    await api('/api/appointments', {
      method: 'POST',
      body: { diagnosticCenterId: Number(centerId), appointmentDate: v.date, testIds: v.testIds },
    })
    await onBooked()
  })

  return (
    <form className="card book" onSubmit={onSubmit} noValidate>
      <Alert>{formError}</Alert>
      <div className="form-row">
        <Field label="Diagnostic center" id="center" error={errors.diagnosticCenterId}>
          <select id="center" className="input" value={centerId} onChange={(e) => setCenterId(e.target.value)} required>
            <option value="">Choose a center…</option>
            {centers?.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </Field>
        <Field label="Date" id="date" error={errors.appointmentDate}>
          <input id="date" className="input" type="date" min={todayIso()} value={values.date} onChange={set('date')} required />
        </Field>
      </div>

      <fieldset className="tests-pick">
        <legend>Tests</legend>
        {!centerId && <p className="muted">Choose a center to see its tests.</p>}
        {testsLoading && <Spinner label="Loading tests…" />}
        {tests?.map((t) => (
          <label className={`test-option ${values.testIds.includes(t.id) ? 'on' : ''}`} key={t.id}>
            <input type="checkbox" checked={values.testIds.includes(t.id)} onChange={() => toggle(t.id)} />
            <span className="test-name">{t.testName}<span className="muted small">normal {t.normalValue} {t.units}</span></span>
            <strong>{formatPrice(t.testPrice)}</strong>
          </label>
        ))}
        {errors.testIds && <span className="field-error">{errors.testIds}</span>}
      </fieldset>

      <div className="book-foot">
        <span>
          {values.testIds.length} test{values.testIds.length === 1 ? '' : 's'} · Total <strong>{formatPrice(total)}</strong>
        </span>
        <button className="btn btn-primary" disabled={busy || !centerId || !values.date || !values.testIds.length}>
          {busy ? 'Booking…' : 'Request appointment'}
        </button>
      </div>
    </form>
  )
}

function ResultsTable({ results, loading }) {
  if (loading) return <Spinner />
  if (!results.length) return <Empty title="No results yet">Results appear here once the center records them.</Empty>
  return (
    <div className="card table-wrap">
      <table className="table">
        <thead>
          <tr><th>Test</th><th>Date</th><th>Center</th><th className="num">Reading</th><th>Normal range</th><th>Condition</th></tr>
        </thead>
        <tbody>
          {results.map((r) => (
            <tr key={r.id}>
              <td>{r.testName}</td>
              <td className="muted">{formatDate(r.date)}</td>
              <td className="muted">{r.center}</td>
              <td className="num"><strong>{r.testReading}</strong> {r.units}</td>
              <td className="muted">{r.normalValue} {r.units}</td>
              <td><ConditionBadge condition={r.condition} /></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function Profile({ onSaved }) {
  const { updateName } = useAuth()
  const { data, loading, error } = useApi('/api/patients/me')
  const { values, set, setValues, errors, busy, formError, submit } = useForm({ name: '', phoneNo: '', age: '', gender: '' })

  useEffect(() => {
    if (data) setValues({ name: data.name, phoneNo: data.phoneNo, age: String(data.age), gender: data.gender })
  }, [data, setValues])

  const onSubmit = submit(async (v) => {
    const saved = await api('/api/patients/me', { method: 'PUT', body: { ...v, age: Number(v.age) || 0 } })
    updateName(saved.name)
    onSaved()
  })

  if (loading) return <Spinner />
  if (error) return <Alert>{error}</Alert>

  return (
    <form className="card narrow" onSubmit={onSubmit} noValidate>
      <Alert>{formError}</Alert>
      <Field label="Email" id="pemail" hint="Your sign-in email can't be changed here.">
        <input id="pemail" className="input" value={data?.email ?? ''} disabled />
      </Field>
      <Field label="Full name" error={errors.name} id="pname">
        <input id="pname" className="input" value={values.name} onChange={set('name')} />
      </Field>
      <div className="form-row">
        <Field label="Phone" error={errors.phoneNo} id="pphone">
          <input id="pphone" className="input" value={values.phoneNo} onChange={set('phoneNo')} />
        </Field>
        <Field label="Age" error={errors.age} id="page">
          <input id="page" className="input" type="number" min="0" max="120" value={values.age} onChange={set('age')} />
        </Field>
      </div>
      <Field label="Gender" error={errors.gender} id="pgender">
        <select id="pgender" className="input" value={values.gender} onChange={set('gender')}>
          <option>Female</option><option>Male</option><option>Other</option>
        </select>
      </Field>
      <button className="btn btn-primary" disabled={busy}>{busy ? 'Saving…' : 'Save changes'}</button>
    </form>
  )
}
