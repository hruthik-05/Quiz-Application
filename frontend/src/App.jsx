import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';


import AdminLayout from './layouts/AdminLayout';
import StudentLayout from './layouts/StudentLayout';


import UserLogin from './pages/UserLogin';
import AdminLogin from './pages/AdminLogin';
import Register from './pages/Register';



import AdminDashboard from './pages/AdminDashboard';
import AdminContest from './pages/AdminContest';
import AdminContestManager from './pages/AdminContestManager';
import AdminContestReport from './pages/AdminContestReport';
import AdminAnalytics from './pages/AdminAnalytics';


import Dashboard from './pages/Dashboard';
import ContestList from './pages/ContestList';
import ContestPlayer from './pages/ContestPlayer';
import QuizSetup from './pages/QuizSetup';
import QuizPlayer from './pages/QuizPlayer';
import Result from './pages/Result';
import UserProfile from './pages/UserProfile';

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="min-h-screen bg-slate-50 text-slate-900">
          <Routes>
            
            <Route path="/login" element={<Navigate to="/user/login" replace />} />
            <Route path="/user/login" element={<UserLogin />} />
            <Route path="/admin/login" element={<AdminLogin />} />
            <Route path="/register" element={<Register />} />


            
            <Route path="/admin" element={<AdminLayout />}>
              <Route index element={<Navigate to="/admin/dashboard" replace />} />
              <Route path="dashboard" element={<AdminDashboard />} />
              <Route path="dashboard" element={<AdminDashboard />} />
              <Route path="contest" element={<AdminContest />} />
              <Route path="contest-manager" element={<AdminContestManager />} />
              <Route path="contest-report/:id" element={<AdminContestReport />} />
              <Route path="analytics" element={<AdminAnalytics />} />
            </Route>

            
            <Route path="/" element={<StudentLayout />}>
              <Route index element={<Navigate to="/dashboard" replace />} />
              <Route path="dashboard" element={<Dashboard />} />
              <Route path="contests" element={<ContestList />} />
              <Route path="contest/:id" element={<ContestPlayer />} />
              <Route path="quiz/setup/:mode" element={<QuizSetup />} />
              <Route path="quiz/play" element={<QuizPlayer />} />
              <Route path="quiz/play" element={<QuizPlayer />} />
              <Route path="quiz/result" element={<Result />} />
              <Route path="profile" element={<UserProfile />} />
            </Route>

            
            <Route path="*" element={<Navigate to="/dashboard" />} />
          </Routes>
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;
