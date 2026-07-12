import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

export default function UserLogin() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        const result = await login(username, password);
        if (result.success) {
            navigate('/dashboard');
        } else {
            setError(result.message);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50">
            <div className="card w-full max-w-md">
                <h2 className="text-2xl font-bold text-center mb-6 text-primary-900">Student Login</h2>
                {error && <div className="bg-red-100 text-red-700 p-3 rounded mb-4 text-sm">{error}</div>}
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium mb-1">Username</label>
                        <input
                            type="text"
                            className="input-field"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium mb-1">Password</label>
                        <input
                            type="password"
                            className="input-field"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn-primary w-full">
                        Login
                    </button>
                </form>

                <div className="relative my-6">
                    <div className="absolute inset-0 flex items-center">
                        <div className="w-full border-t border-slate-200"></div>
                    </div>
                    <div className="relative flex justify-center text-sm">
                        <span className="bg-white px-2 text-slate-500">Or continue with</span>
                    </div>
                </div>

                <button
                    onClick={() => window.location.href = 'http://localhost:8200/oauth2/authorization/google'}
                    className="w-full flex items-center justify-center gap-2 bg-white border border-slate-300 rounded-md py-2 px-4 text-sm font-medium text-slate-700 hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 shadow-sm transition-all duration-200 hover:shadow-md cursor-pointer"
                >
                    <svg className="w-5 h-5" viewBox="0 0 24 24">
                        <path
                            fill="#EA4335"
                            d="M12 5.04c1.62 0 3.06.56 4.2 1.66l3.12-3.12C17.43 1.83 14.91 1 12 1 7.24 1 3.2 3.73 1.24 7.74l3.75 2.91C5.9 7.6 8.7 5.04 12 5.04z"
                        />
                        <path
                            fill="#4285F4"
                            d="M23.49 12.27c0-.81-.07-1.59-.2-2.36H12v4.51h6.43c-.28 1.44-1.09 2.67-2.3 3.49l3.58 2.78c2.1-1.94 3.3-4.79 3.3-8.42z"
                        />
                        <path
                            fill="#FBBC05"
                            d="M4.99 14.65c-.24-.72-.38-1.49-.38-2.29s.14-1.57.38-2.29L1.24 7.15C.44 8.75 0 10.53 0 12.4s.44 3.65 1.24 5.25l3.75-3z"
                        />
                        <path
                            fill="#34A853"
                            d="M12 23c3.24 0 5.97-1.07 7.96-2.91l-3.58-2.78c-.99.66-2.26 1.06-3.79 1.06-3.3 0-6.1-2.56-7.1-5.61l-3.75 2.9C3.2 19.8 7.24 23 12 23z"
                        />
                    </svg>
                    <span>Sign in with Google</span>
                </button>

                <p className="mt-4 text-center text-sm text-slate-600">
                    Don't have an account? <Link to="/register" className="text-primary-600 hover:underline">Register</Link>
                </p>

            </div>
        </div>
    );
}
