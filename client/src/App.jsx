import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import Layout from './components/Layout'
import Home from './pages/Home'
import Centers from './pages/Centers'
import SignIn from './pages/SignIn'
import SignUp from './pages/SignUp'
import PatientDashboard from './pages/PatientDashboard'
import AdminDashboard from './pages/AdminDashboard'
import NotFound from './pages/NotFound'
import { useAuth, dashboardPath } from './auth'

function RequireRole({ role, children }) {
  const { user } = useAuth()
  const location = useLocation()
  if (!user) return <Navigate to="/signin" replace state={{ from: location.pathname }} />
  if (user.role !== role) return <Navigate to={dashboardPath(user)} replace />
  return children
}

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="centers" element={<Centers />} />
        <Route path="signin" element={<SignIn />} />
        <Route path="signup" element={<SignUp />} />
        <Route path="dashboard" element={<RequireRole role="Patient"><PatientDashboard /></RequireRole>} />
        <Route path="admin" element={<RequireRole role="Admin"><AdminDashboard /></RequireRole>} />
        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  )
}
