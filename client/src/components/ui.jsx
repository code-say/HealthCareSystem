import { useEffect, useRef } from 'react'

export function Spinner({ label = 'Loading…' }) {
  return (
    <div className="spinner-wrap" role="status">
      <span className="spinner" aria-hidden="true" />
      <span>{label}</span>
    </div>
  )
}

export function Alert({ kind = 'error', children, onClose }) {
  if (!children) return null
  return (
    <div className={`alert alert-${kind}`} role={kind === 'error' ? 'alert' : 'status'}>
      <span>{children}</span>
      {onClose && <button className="alert-close" onClick={onClose} aria-label="Dismiss">×</button>}
    </div>
  )
}

const STATUS_LABEL = { Pending: 'Pending', Approved: 'Approved', Rejected: 'Rejected', Cancelled: 'Cancelled' }
export function StatusBadge({ status }) {
  return <span className={`badge badge-${status?.toLowerCase()}`}>{STATUS_LABEL[status] ?? status}</span>
}

export function ConditionBadge({ condition }) {
  const c = (condition || '').toLowerCase()
  const kind = c.includes('normal') ? 'approved' : c.includes('high') || c.includes('low') || c.includes('abnormal') ? 'rejected' : 'pending'
  return <span className={`badge badge-${kind}`}>{condition}</span>
}

export function Field({ label, error, hint, children, id }) {
  return (
    <label className={`field ${error ? 'has-error' : ''}`} htmlFor={id}>
      <span className="field-label">{label}</span>
      {children}
      {error ? <span className="field-error">{error}</span> : hint ? <span className="field-hint">{hint}</span> : null}
    </label>
  )
}

export function Empty({ title, children, action }) {
  return (
    <div className="empty">
      <div className="empty-icon" aria-hidden="true">＋</div>
      <h3>{title}</h3>
      {children && <p>{children}</p>}
      {action}
    </div>
  )
}

export function Modal({ title, onClose, children, footer }) {
  const ref = useRef(null)
  useEffect(() => {
    const prev = document.activeElement
    ref.current?.querySelector('input, select, textarea, button')?.focus()
    const onKey = (e) => e.key === 'Escape' && onClose()
    document.addEventListener('keydown', onKey)
    document.body.style.overflow = 'hidden'
    return () => {
      document.removeEventListener('keydown', onKey)
      document.body.style.overflow = ''
      prev?.focus?.()
    }
  }, [onClose])

  return (
    <div className="modal-backdrop" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal" role="dialog" aria-modal="true" aria-label={title} ref={ref}>
        <div className="modal-head">
          <h2>{title}</h2>
          <button className="icon-btn" onClick={onClose} aria-label="Close">×</button>
        </div>
        <div className="modal-body">{children}</div>
        {footer && <div className="modal-foot">{footer}</div>}
      </div>
    </div>
  )
}

export function Confirm({ title, message, confirmLabel = 'Delete', onConfirm, onCancel, busy }) {
  return (
    <Modal
      title={title}
      onClose={onCancel}
      footer={
        <>
          <button className="btn btn-ghost" onClick={onCancel} disabled={busy}>Cancel</button>
          <button className="btn btn-danger" onClick={onConfirm} disabled={busy}>{busy ? 'Working…' : confirmLabel}</button>
        </>
      }
    >
      <p>{message}</p>
    </Modal>
  )
}

export function Tabs({ tabs, active, onChange }) {
  return (
    <div className="tabs" role="tablist">
      {tabs.map((t) => (
        <button
          key={t.key}
          role="tab"
          aria-selected={active === t.key}
          className={`tab ${active === t.key ? 'active' : ''}`}
          onClick={() => onChange(t.key)}
        >
          {t.label}
          {t.count != null && <span className="tab-count">{t.count}</span>}
        </button>
      ))}
    </div>
  )
}
