// End-to-end check against the real API: patient books, admin approves and records a result,
// patient sees it. Run by CI with the API on :5080 and `vite preview` on :5173.
import { chromium } from 'playwright'

const BASE = process.env.E2E_BASE || 'http://localhost:5173'
const errors = []
const step = (msg) => console.log(`\n▶ ${msg}`)

const browser = await chromium.launch()
const page = await browser.newPage({ viewport: { width: 1280, height: 900 } })
page.on('pageerror', (e) => errors.push(`pageerror: ${e.message}`))
page.on('console', (m) => m.type() === 'error' && errors.push(`console: ${m.text()}`))

async function signIn(email, password) {
  await page.goto(`${BASE}/signin`)
  await page.getByLabel('Email').fill(email)
  await page.getByLabel('Password').fill(password)
  await page.getByRole('button', { name: 'Sign in' }).click()
}
async function signOut() {
  await page.getByRole('button', { name: 'Sign out' }).click()
  await page.waitForURL(`${BASE}/`)
}

try {
  step('Home page lists centers from the API')
  await page.goto(BASE)
  await page.getByText('CareFirst Diagnostics').first().waitFor()

  step('Centers page shows tests with prices')
  await page.goto(`${BASE}/centers`)
  await page.getByText('Sahyadri Path Labs').waitFor()
  await page.getByRole('cell', { name: 'Vitamin D (25-OH)' }).first().waitFor()

  step('Wrong password shows an error')
  await signIn('patient@healthcare.demo', 'wrong-password')
  await page.getByText('Incorrect email or password.').waitFor()

  step('Patient signs in and books an appointment')
  await signIn('patient@healthcare.demo', 'Patient@12345')
  await page.waitForURL(`${BASE}/dashboard`)
  await page.getByRole('heading', { name: /Hello, Ananya/ }).waitFor()
  await page.getByRole('tab', { name: 'Book' }).click()
  await page.getByLabel('Diagnostic center').selectOption({ label: 'Lakeview Health Centre' })
  await page.getByText('Haemoglobin').waitFor()
  const date = new Date(Date.now() + 3 * 86400000).toISOString().slice(0, 10)
  await page.getByLabel('Date').fill(date)
  await page.locator('.test-option', { hasText: 'Haemoglobin' }).click()
  await page.locator('.test-option', { hasText: 'Thyroid (TSH)' }).click()
  await page.getByRole('button', { name: 'Request appointment' }).click()
  await page.getByText('Appointment requested.').waitFor()
  const card = page.locator('.appt', { hasText: 'Lakeview Health Centre' }).first()
  await card.getByText('Pending').waitFor()
  await signOut()

  step('Admin approves it and records a result')
  await signIn('admin@healthcare.demo', 'Admin@12345')
  await page.waitForURL(`${BASE}/admin`)
  const pending = page.locator('.appt', { hasText: 'Lakeview Health Centre' }).filter({ hasText: 'Ananya Sharma' }).first()
  await pending.getByRole('button', { name: 'Approve' }).click()
  await page.locator('.segmented button', { hasText: 'Approved' }).click()
  const approved = page.locator('.appt', { hasText: 'Lakeview Health Centre' }).filter({ hasText: 'Ananya Sharma' }).first()
  await approved.getByRole('button', { name: 'Add result' }).click()
  const dialog = page.getByRole('dialog')
  await dialog.getByLabel('Test').selectOption({ label: 'Haemoglobin' })
  await dialog.getByLabel(/Reading/).fill('13.4')
  await dialog.getByRole('button', { name: 'Save result' }).click()
  await dialog.waitFor({ state: 'detached' })
  await approved.getByText('13.4 g/dL').waitFor()

  step('Admin can add a center')
  await page.getByRole('tab', { name: 'Centers' }).click()
  await page.getByRole('button', { name: 'Add center' }).click()
  const cdlg = page.getByRole('dialog')
  await cdlg.getByLabel('Name').fill('E2E Test Clinic')
  await cdlg.getByLabel('Address').fill('1 Test Lane, Chennai')
  await cdlg.getByLabel('Phone').fill('+91 44 1234 5678')
  await cdlg.getByLabel('Email').fill('e2e@clinic.demo')
  await cdlg.getByRole('button', { name: 'Save' }).click()
  await page.getByRole('cell', { name: 'E2E Test Clinic' }).waitFor()
  await signOut()

  step('Patient sees the approved appointment and result')
  await signIn('patient@healthcare.demo', 'Patient@12345')
  await page.waitForURL(`${BASE}/dashboard`)
  const mine = page.locator('.appt', { hasText: 'Lakeview Health Centre' }).first()
  await mine.getByText('Approved').waitFor()
  await mine.getByText('13.4 g/dL').waitFor()
  await page.getByRole('tab', { name: /Results/ }).click()
  await page.getByRole('cell', { name: 'Haemoglobin' }).waitFor()

  step('A patient cannot open the admin page')
  await page.goto(`${BASE}/admin`)
  await page.waitForURL(`${BASE}/dashboard`)

  await page.screenshot({ path: 'e2e-final.png', fullPage: true })
  const real = errors.filter((e) => !/Failed to load resource.*(400|401)/.test(e))
  if (real.length) throw new Error(`Browser errors:\n${real.join('\n')}`)
  console.log('\n✅ E2E flow passed')
} catch (e) {
  await page.screenshot({ path: 'e2e-failure.png', fullPage: true }).catch(() => {})
  console.error(`\n❌ ${e.message}`)
  console.error(`URL: ${page.url()}`)
  console.error((await page.locator('body').innerText().catch(() => '')).slice(0, 1500))
  process.exitCode = 1
} finally {
  await browser.close()
}
