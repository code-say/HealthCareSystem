import { useState } from 'react'
import { api, formatDate, formatPrice } from '../api'
import { useApi, useForm } from '../hooks'
import { Alert, ConditionBadge, Confirm, Empty, Field, Modal, Spinner, StatusBadge, Tabs } from '../components/ui'

export default function AdminDashboard() {
  const [tab, setTab] = useState('appointments')
  const stats = useApi('/api/stats')
  const s = stats.data

  return (
    <div className="container page">
      <div className="page-head">
        <div>
          <p className="eyebrow">Admin dashboard</p>
          <h1>Manage the health care system</h1>
        </div>
      </div>

      <div className="stats six">
        <div className="stat"><span>Pending</span><strong>{s?.pendingAppointments ?? '–'}</strong></div>
        <div className="stat"><span>Approved</span><strong>{s?.approvedAppointments ?? '–'}</strong></div>
        <div className="stat"><span>Results</span><strong>{s?.results ?? '–'}</strong></div>
        <div className="stat"><span>Patients</span><strong>{s?.patients ?? '–'}</strong></div>
        <div className="stat"><span>Centers</span><strong>{s?.centers ?? '–'}</strong></div>
        <div className="stat"><span>Tests</span><strong>{s?.tests ?? '–'}</strong></div>
      </div>

      <Tabs
        active={tab}
        onChange={setTab}
        tabs={[
          { key: 'appointments', label: 'Appointments', count: s?.pendingAppointments || undefined },
          { key: 'centers', label: 'Centers' },
          { key: 'tests', label: 'Tests' },
          { key: 'patients', label: 'Patients' },
          { key: 'results', label: 'Results' },
        ]}
      />

      {tab === 'appointments' && <Appointments onChange={stats.reload} />}
      {tab === 'centers' && <CentersAdmin onChange={stats.reload} />}
      {tab === 'tests' && <TestsAdmin onChange={stats.reload} />}
      {tab === 'patients' && <PatientsAdmin onChange={stats.reload} />}
      {tab === 'results' && <ResultsAdmin onChange={stats.reload} />}
    </div>
  )
}

// ---------------- Appointments ----------------

function Appointments({ onChange }) {
  const [status, setStatus] = useState('Pending')
  const { data, error, loading, reload } = useApi(`/api/appointments${status ? `?status=${status}` : ''}`)
  const [busy, setBusy] = useState(null)
  const [err, setErr] = useState(null)
  const [resultFor, setResultFor] = useState(null)

  const act = async (id, fn) => {
    setBusy(id)
    setErr(null)
    try {
      await fn()
      await reload()
      onChange()
    } catch (e) {
      setErr(e.message)
    } finally {
      setBusy(null)
    }
  }
  const setStatusOf = (id, s) => act(id, () => api(`/api/appointments/${id}/status`, { method: 'PUT', body: { status: s } }))

  return (
    <>
      <div className="toolbar">
        <div className="segmented" role="group" aria-label="Filter by status">
          {['Pending', 'Approved', 'Rejected', 'Cancelled', ''].map((s) => (
            <button key={s || 'all'} className={status === s ? 'on' : ''} onClick={() => setStatus(s)}>{s || 'All'}</button>
          ))}
        </div>
      </div>
      <Alert>{err || error}</Alert>
      {loading && !data && <Spinner />}
      {data && !data.length && <Empty title={`No ${status.toLowerCase() || ''} appointments`}>Nothing to review right now.</Empty>}

      <div className="stack">
        {data?.map((a) => {
          const missing = a.tests.filter((t) => !a.results.some((r) => r.diagnosticTestId === t.id))
          return (
            <article className="card appt" key={a.id}>
              <div className="appt-head">
                <div>
                  <h3>{a.patientName}</h3>
                  <p className="muted">{a.centerName} · {formatDate(a.appointmentDate)} · #{a.id}</p>
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
                        <span className="result"><strong>{r.testReading} {r.units}</strong><ConditionBadge condition={r.condition} /></span>
                      ) : (
                        <span className="muted">{formatPrice(t.testPrice)}</span>
                      )}
                    </li>
                  )
                })}
              </ul>
              <div className="appt-foot">
                <span>Total <strong>{formatPrice(a.totalPrice)}</strong></span>
                <div className="actions">
                  {a.status === 'Pending' && (
                    <>
                      <button className="btn btn-ghost btn-sm" disabled={busy === a.id} onClick={() => setStatusOf(a.id, 'Rejected')}>Reject</button>
                      <button className="btn btn-primary btn-sm" disabled={busy === a.id} onClick={() => setStatusOf(a.id, 'Approved')}>Approve</button>
                    </>
                  )}
                  {a.status === 'Rejected' && (
                    <button className="btn btn-ghost btn-sm" disabled={busy === a.id} onClick={() => setStatusOf(a.id, 'Approved')}>Approve instead</button>
                  )}
                  {a.status === 'Approved' && missing.length > 0 && (
                    <button className="btn btn-primary btn-sm" onClick={() => setResultFor({ appointment: a, tests: missing })}>Add result</button>
                  )}
                </div>
              </div>
            </article>
          )
        })}
      </div>

      {resultFor && (
        <ResultModal
          appointment={resultFor.appointment}
          tests={resultFor.tests}
          onClose={() => setResultFor(null)}
          onSaved={async () => {
            setResultFor(null)
            await reload()
            onChange()
          }}
        />
      )}
    </>
  )
}

function ResultModal({ appointment, tests, onClose, onSaved }) {
  const { values, set, errors, busy, formError, submit } = useForm({ diagnosticTestId: String(tests[0].id), testReading: '', condition: 'Normal' })
  const test = tests.find((t) => String(t.id) === values.diagnosticTestId)

  const onSubmit = submit(async (v) => {
    await api('/api/test-results', {
      method: 'POST',
      body: { appointmentId: appointment.id, diagnosticTestId: Number(v.diagnosticTestId), testReading: Number(v.testReading), condition: v.condition },
    })
    await onSaved()
  })

  return (
    <Modal
      title={`Add result · ${appointment.patientName}`}
      onClose={onClose}
      footer={<><button className="btn btn-ghost" onClick={onClose}>Cancel</button><button className="btn btn-primary" form="result-form" disabled={busy || values.testReading === ''}>{busy ? 'Saving…' : 'Save result'}</button></>}
    >
      <form id="result-form" onSubmit={onSubmit} noValidate>
        <Alert>{formError}</Alert>
        <Field label="Test" id="rt">
          <select id="rt" className="input" value={values.diagnosticTestId} onChange={set('diagnosticTestId')}>
            {tests.map((t) => <option key={t.id} value={t.id}>{t.testName}</option>)}
          </select>
        </Field>
        <div className="form-row">
          <Field label={`Reading${test?.units ? ` (${test.units})` : ''}`} error={errors.testReading} hint={test ? `Normal: ${test.normalValue} ${test.units}` : null} id="rr">
            <input id="rr" className="input" type="number" step="any" value={values.testReading} onChange={set('testReading')} />
          </Field>
          <Field label="Condition" error={errors.condition} id="rc">
            <select id="rc" className="input" value={values.condition} onChange={set('condition')}>
              <option>Normal</option><option>High</option><option>Low</option><option>Borderline</option>
            </select>
          </Field>
        </div>
      </form>
    </Modal>
  )
}

// ---------------- Generic CRUD helpers ----------------

function useCrud(path, onChange) {
  const list = useApi(path)
  const [editing, setEditing] = useState(null) // null | {} (new) | item
  const [deleting, setDeleting] = useState(null)
  const [busy, setBusy] = useState(false)
  const [err, setErr] = useState(null)

  const remove = async (url) => {
    setBusy(true)
    setErr(null)
    try {
      await api(url, { method: 'DELETE' })
      setDeleting(null)
      await list.reload()
      onChange()
    } catch (e) {
      setErr(e.message)
      setDeleting(null)
    } finally {
      setBusy(false)
    }
  }
  const saved = async () => {
    setEditing(null)
    await list.reload()
    onChange()
  }
  return { list, editing, setEditing, deleting, setDeleting, busy, err, setErr, remove, saved }
}

function RowActions({ onEdit, onDelete }) {
  return (
    <td className="row-actions">
      {onEdit && <button className="btn btn-ghost btn-sm" onClick={onEdit}>Edit</button>}
      <button className="btn btn-ghost btn-sm danger" onClick={onDelete}>Delete</button>
    </td>
  )
}

// ---------------- Centers ----------------

function CentersAdmin({ onChange }) {
  const c = useCrud('/api/centers', onChange)
  return (
    <>
      <div className="toolbar"><button className="btn btn-primary" onClick={() => c.setEditing({})}>Add center</button></div>
      <Alert onClose={() => c.setErr(null)}>{c.err || c.list.error}</Alert>
      {c.list.loading && !c.list.data && <Spinner />}
      <div className="card table-wrap">
        <table className="table">
          <thead><tr><th>Name</th><th>Address</th><th>Contact</th><th className="num">Tests</th><th /></tr></thead>
          <tbody>
            {c.list.data?.map((x) => (
              <tr key={x.id}>
                <td><strong>{x.name}</strong></td>
                <td className="muted">{x.address}</td>
                <td className="muted">{x.contactNo}<br />{x.contactEmail}</td>
                <td className="num">{x.testCount}</td>
                <RowActions onEdit={() => c.setEditing(x)} onDelete={() => c.setDeleting(x)} />
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {c.editing && <CenterForm center={c.editing} onClose={() => c.setEditing(null)} onSaved={c.saved} />}
      {c.deleting && (
        <Confirm
          title="Delete center?"
          message={`${c.deleting.name} and all its tests, appointments and results will be deleted.`}
          busy={c.busy}
          onCancel={() => c.setDeleting(null)}
          onConfirm={() => c.remove(`/api/centers/${c.deleting.id}`)}
        />
      )}
    </>
  )
}

function CenterForm({ center, onClose, onSaved }) {
  const isNew = !center.id
  const { values, set, errors, busy, formError, submit } = useForm({
    name: center.name ?? '', address: center.address ?? '', contactNo: center.contactNo ?? '', contactEmail: center.contactEmail ?? '',
  })
  const onSubmit = submit(async (v) => {
    await api(isNew ? '/api/centers' : `/api/centers/${center.id}`, { method: isNew ? 'POST' : 'PUT', body: v })
    await onSaved()
  })
  return (
    <Modal
      title={isNew ? 'Add center' : 'Edit center'}
      onClose={onClose}
      footer={<><button className="btn btn-ghost" onClick={onClose}>Cancel</button><button className="btn btn-primary" form="center-form" disabled={busy}>{busy ? 'Saving…' : 'Save'}</button></>}
    >
      <form id="center-form" onSubmit={onSubmit} noValidate>
        <Alert>{formError}</Alert>
        <Field label="Name" error={errors.name} id="cn"><input id="cn" className="input" value={values.name} onChange={set('name')} /></Field>
        <Field label="Address" error={errors.address} id="ca"><input id="ca" className="input" value={values.address} onChange={set('address')} /></Field>
        <div className="form-row">
          <Field label="Phone" error={errors.contactNo} id="cp"><input id="cp" className="input" value={values.contactNo} onChange={set('contactNo')} /></Field>
          <Field label="Email" error={errors.contactEmail} id="ce"><input id="ce" className="input" type="email" value={values.contactEmail} onChange={set('contactEmail')} /></Field>
        </div>
      </form>
    </Modal>
  )
}

// ---------------- Tests ----------------

function TestsAdmin({ onChange }) {
  const [centerId, setCenterId] = useState('')
  const centers = useApi('/api/centers')
  const c = useCrud(`/api/tests${centerId ? `?centerId=${centerId}` : ''}`, onChange)
  return (
    <>
      <div className="toolbar">
        <select className="input auto" value={centerId} onChange={(e) => setCenterId(e.target.value)} aria-label="Filter by center">
          <option value="">All centers</option>
          {centers.data?.map((x) => <option key={x.id} value={x.id}>{x.name}</option>)}
        </select>
        <button className="btn btn-primary" onClick={() => c.setEditing({ diagnosticCenterId: centerId })}>Add test</button>
      </div>
      <Alert onClose={() => c.setErr(null)}>{c.err || c.list.error}</Alert>
      {c.list.loading && !c.list.data && <Spinner />}
      <div className="card table-wrap">
        <table className="table">
          <thead><tr><th>Test</th><th>Center</th><th>Normal range</th><th className="num">Price</th><th /></tr></thead>
          <tbody>
            {c.list.data?.map((t) => (
              <tr key={t.id}>
                <td><strong>{t.testName}</strong></td>
                <td className="muted">{t.centerName}</td>
                <td className="muted">{t.normalValue} {t.units}</td>
                <td className="num">{formatPrice(t.testPrice)}</td>
                <RowActions onEdit={() => c.setEditing(t)} onDelete={() => c.setDeleting(t)} />
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {c.editing && <TestForm test={c.editing} centers={centers.data ?? []} onClose={() => c.setEditing(null)} onSaved={c.saved} />}
      {c.deleting && (
        <Confirm
          title="Delete test?"
          message={`${c.deleting.testName} at ${c.deleting.centerName} will be removed, including any results recorded for it.`}
          busy={c.busy}
          onCancel={() => c.setDeleting(null)}
          onConfirm={() => c.remove(`/api/tests/${c.deleting.id}`)}
        />
      )}
    </>
  )
}

function TestForm({ test, centers, onClose, onSaved }) {
  const isNew = !test.id
  const { values, set, errors, busy, formError, submit } = useForm({
    testName: test.testName ?? '', testPrice: test.testPrice ?? '', normalValue: test.normalValue ?? '', units: test.units ?? '',
    diagnosticCenterId: String(test.diagnosticCenterId ?? ''),
  })
  const onSubmit = submit(async (v) => {
    const body = { ...v, testPrice: Number(v.testPrice) || 0, diagnosticCenterId: Number(v.diagnosticCenterId) || 0 }
    await api(isNew ? '/api/tests' : `/api/tests/${test.id}`, { method: isNew ? 'POST' : 'PUT', body })
    await onSaved()
  })
  return (
    <Modal
      title={isNew ? 'Add test' : 'Edit test'}
      onClose={onClose}
      footer={<><button className="btn btn-ghost" onClick={onClose}>Cancel</button><button className="btn btn-primary" form="test-form" disabled={busy}>{busy ? 'Saving…' : 'Save'}</button></>}
    >
      <form id="test-form" onSubmit={onSubmit} noValidate>
        <Alert>{formError}</Alert>
        <Field label="Center" error={errors.diagnosticCenterId} id="tc">
          <select id="tc" className="input" value={values.diagnosticCenterId} onChange={set('diagnosticCenterId')}>
            <option value="">Choose…</option>
            {centers.map((x) => <option key={x.id} value={x.id}>{x.name}</option>)}
          </select>
        </Field>
        <Field label="Test name" error={errors.testName} id="tn"><input id="tn" className="input" value={values.testName} onChange={set('testName')} /></Field>
        <div className="form-row three">
          <Field label="Price (₹)" error={errors.testPrice} id="tp"><input id="tp" className="input" type="number" min="0" value={values.testPrice} onChange={set('testPrice')} /></Field>
          <Field label="Normal range" error={errors.normalValue} id="tv"><input id="tv" className="input" placeholder="70-100" value={values.normalValue} onChange={set('normalValue')} /></Field>
          <Field label="Units" error={errors.units} id="tu"><input id="tu" className="input" placeholder="mg/dL" value={values.units} onChange={set('units')} /></Field>
        </div>
      </form>
    </Modal>
  )
}

// ---------------- Patients ----------------

function PatientsAdmin({ onChange }) {
  const c = useCrud('/api/patients', onChange)
  return (
    <>
      <div className="toolbar"><button className="btn btn-primary" onClick={() => c.setEditing({})}>Add walk-in patient</button></div>
      <Alert onClose={() => c.setErr(null)}>{c.err || c.list.error}</Alert>
      {c.list.loading && !c.list.data && <Spinner />}
      <div className="card table-wrap">
        <table className="table">
          <thead><tr><th>Name</th><th>Contact</th><th>Age / gender</th><th className="num">Appointments</th><th /></tr></thead>
          <tbody>
            {c.list.data?.map((p) => (
              <tr key={p.id}>
                <td><strong>{p.name}</strong>{!p.email && <span className="tag">walk-in</span>}</td>
                <td className="muted">{p.phoneNo}{p.email && <><br />{p.email}</>}</td>
                <td className="muted">{p.age} · {p.gender}</td>
                <td className="num">{p.appointmentCount}</td>
                <RowActions onEdit={() => c.setEditing(p)} onDelete={() => c.setDeleting(p)} />
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {c.editing && <PatientForm patient={c.editing} onClose={() => c.setEditing(null)} onSaved={c.saved} />}
      {c.deleting && (
        <Confirm
          title="Delete patient?"
          message={`${c.deleting.name}${c.deleting.email ? ', their login' : ''} and all their appointments will be deleted.`}
          busy={c.busy}
          onCancel={() => c.setDeleting(null)}
          onConfirm={() => c.remove(`/api/patients/${c.deleting.id}`)}
        />
      )}
    </>
  )
}

function PatientForm({ patient, onClose, onSaved }) {
  const isNew = !patient.id
  const { values, set, errors, busy, formError, submit } = useForm({
    name: patient.name ?? '', phoneNo: patient.phoneNo ?? '', age: String(patient.age ?? ''), gender: patient.gender ?? 'Female',
  })
  const onSubmit = submit(async (v) => {
    await api(isNew ? '/api/patients' : `/api/patients/${patient.id}`, { method: isNew ? 'POST' : 'PUT', body: { ...v, age: Number(v.age) || 0 } })
    await onSaved()
  })
  return (
    <Modal
      title={isNew ? 'Add walk-in patient' : 'Edit patient'}
      onClose={onClose}
      footer={<><button className="btn btn-ghost" onClick={onClose}>Cancel</button><button className="btn btn-primary" form="patient-form" disabled={busy}>{busy ? 'Saving…' : 'Save'}</button></>}
    >
      <form id="patient-form" onSubmit={onSubmit} noValidate>
        <Alert>{formError}</Alert>
        <Field label="Full name" error={errors.name} id="pn"><input id="pn" className="input" value={values.name} onChange={set('name')} /></Field>
        <div className="form-row three">
          <Field label="Phone" error={errors.phoneNo} id="pp"><input id="pp" className="input" value={values.phoneNo} onChange={set('phoneNo')} /></Field>
          <Field label="Age" error={errors.age} id="pa"><input id="pa" className="input" type="number" min="0" max="120" value={values.age} onChange={set('age')} /></Field>
          <Field label="Gender" error={errors.gender} id="pg">
            <select id="pg" className="input" value={values.gender} onChange={set('gender')}><option>Female</option><option>Male</option><option>Other</option></select>
          </Field>
        </div>
      </form>
    </Modal>
  )
}

// ---------------- Results ----------------

function ResultsAdmin({ onChange }) {
  const c = useCrud('/api/test-results', onChange)
  if (c.list.data && !c.list.data.length)
    return <Empty title="No results recorded">Approve an appointment, then use “Add result” on it.</Empty>
  return (
    <>
      <Alert onClose={() => c.setErr(null)}>{c.err || c.list.error}</Alert>
      {c.list.loading && !c.list.data && <Spinner />}
      <div className="card table-wrap">
        <table className="table">
          <thead><tr><th>Patient</th><th>Test</th><th className="num">Reading</th><th>Normal range</th><th>Condition</th><th>Appt.</th><th /></tr></thead>
          <tbody>
            {c.list.data?.map((r) => (
              <tr key={r.id}>
                <td><strong>{r.patientName}</strong></td>
                <td>{r.testName}</td>
                <td className="num">{r.testReading} {r.units}</td>
                <td className="muted">{r.normalValue} {r.units}</td>
                <td><ConditionBadge condition={r.condition} /></td>
                <td className="muted">#{r.appointmentId}</td>
                <RowActions onDelete={() => c.setDeleting(r)} />
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {c.deleting && (
        <Confirm
          title="Delete result?"
          message={`The ${c.deleting.testName} result for ${c.deleting.patientName} will be removed.`}
          busy={c.busy}
          onCancel={() => c.setDeleting(null)}
          onConfirm={() => c.remove(`/api/test-results/${c.deleting.id}`)}
        />
      )}
    </>
  )
}
