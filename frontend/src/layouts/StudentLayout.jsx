import { Navigate, Outlet, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function StudentLayout() {
    const { user, logout, loading } = useAuth();

    if (loading) {
        return <div className="h-screen flex items-center justify-center">Loading...</div>;
    }

    if (!user) {
        return <Navigate to="/user/login" replace />;
    }



    const isAdmin = user.roles?.some(r => r === 'ADMIN' || r === 'ROLE_ADMIN');
    if (isAdmin) {
        return <Navigate to="/admin/dashboard" replace />;
    }

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col">
            
            <header className="bg-white shadow-sm z-10">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16 items-center">
                        <Link to="/dashboard" className="text-2xl font-bold bg-gradient-to-r from-primary-600 to-indigo-600 bg-clip-text text-transparent">
                            QuizPlatform
                        </Link>
                        <nav className="hidden md:flex space-x-8">
                            <Link to="/dashboard" className="text-slate-600 hover:text-primary-600 px-3 py-2 font-medium">Dashboard</Link>
                            <Link to="/contests" className="text-slate-600 hover:text-primary-600 px-3 py-2 font-medium">Exams</Link>
                            <Link to="/quiz/setup/custom" className="text-slate-600 hover:text-primary-600 px-3 py-2 font-medium">Practice</Link>
                        </nav>
                        <div className="flex items-center space-x-4">
                            <Link to="/profile" className="text-sm text-slate-600 hover:text-primary-600">
                                {user.username}
                            </Link>
                            <button
                                onClick={logout}
                                className="text-sm font-medium text-red-600 hover:text-red-700 bg-red-50 hover:bg-red-100 px-3 py-1.5 rounded transition"
                            >
                                Logout
                            </button>
                        </div>
                    </div>
                </div>
            </header>

            
            <main className="flex-1 w-full max-w-7xl mx-auto px-4 py-6">
                <Outlet />
            </main>
        </div>
    );
}
