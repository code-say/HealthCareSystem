import { useCallback, useEffect, useState } from 'react'
import { api } from './api'

/** Load data from the API with loading/error state and a reload function. */
export function useApi(path, { skip = false } = {}) {
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(!skip)

  const load = useCallback(async () => {
    if (skip || !path) return
    setLoading(true)
    setError(null)
    try {
      setData(await api(path))
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [path, skip])

  useEffect(() => {
    load()
  }, [load])

  return { data, setData, error, loading, reload: load }
}

/** Form state helper with per-field errors from the API. */
export function useForm(initial) {
  const [values, setValues] = useState(initial)
  const [errors, setErrors] = useState({})
  const [busy, setBusy] = useState(false)
  const [formError, setFormError] = useState(null)

  const set = (name) => (e) => {
    const v = e?.target ? (e.target.type === 'checkbox' ? e.target.checked : e.target.value) : e
    setValues((s) => ({ ...s, [name]: v }))
    setErrors((s) => ({ ...s, [name]: undefined }))
  }

  const submit = (fn) => async (e) => {
    e?.preventDefault()
    setBusy(true)
    setFormError(null)
    setErrors({})
    try {
      await fn(values)
    } catch (err) {
      setErrors(err.fieldErrors || {})
      setFormError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return { values, setValues, set, errors, busy, formError, setFormError, submit }
}
