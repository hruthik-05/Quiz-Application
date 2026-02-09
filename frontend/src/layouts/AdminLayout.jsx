import { Navigate, Outlet, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function AdminLayout() {
    const { user, loading, logout } = useAuth();
    const navigate = import('react-router-dom').useNavigate; 

    if (loading) {
        return <div className="h-screen flex items-center justify-center">Loading...</div>;
    }


    const isAdmin = user?.roles?.some(role => role === 'ADMIN' || role === 'ROLE_ADMIN');

    if (!user) {
        return <Navigate to="/admin/login" replace />;
    }

    if (!isAdmin) {
        return <Navigate to="/dashboard" replace />;
    }

    return (
        <div className="flex h-screen bg-slate-100">
            
            <aside className="w-64 bg-slate-900 text-white flex flex-col">
                <div className="p-6 text-xl font-bold border-b border-slate-800">
                    Admin Panel
                </div>
                <nav className="flex-1 p-4 space-y-2">
                    <Link to="/admin/dashboard" className="block px-4 py-3 rounded hover:bg-slate-800 transition">
                        Dashboard
                    </Link>
                    <Link to="/admin/contest-manager" className="block px-4 py-3 rounded hover:bg-slate-800 transition">
                        Contests
                    </Link>
                    <Link to="/admin/analytics" className="block px-4 py-3 rounded hover:bg-slate-800 transition">
                        Global Analytics
                    </Link>
                </nav>
                <div className="p-4 border-t border-slate-800">
                    <div className="text-sm text-slate-400">Logged in as:</div>
                    <div className="font-bold mb-2">{user.username}</div>
                    <button
                        onClick={() => {
                            if (window.confirm('Logout?')) {
                                user?.roles ? null : null; 
                                window.location.href = '/user/login';

                                localStorage.removeItem('user');
                                localStorage.removeItem('token');
                            }
                        }}
                        className="text-xs bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded"
                    >
                        Logout
                    </button>
                </div>
            </aside>

            
            <main className="flex-1 overflow-auto p-8">
                <Outlet />
            </main>
        </div>
    );
}
