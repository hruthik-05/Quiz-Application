import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, BarChart, Bar } from 'recharts';

export default function Dashboard() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [stats, setStats] = useState(null);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const res = await api.get(`/analytics/${user.id}`);
                setStats(res.data);
            } catch (err) {
                console.error("Failed to fetch stats", err);
            }
        };
        if (user?.id) fetchStats();
    }, [user]);

    const subjectData = stats?.subjectAccuracy ? Object.keys(stats.subjectAccuracy).map(subject => ({
        name: subject,
        accuracy: Math.round(stats.subjectAccuracy[subject]),
        attempts: stats.subjectAttempts[subject]
    })) : [];

    return (
        <div className="p-8 max-w-7xl mx-auto">
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-primary-900">Welcome, {user?.username}</h1>
                    <p className="text-slate-600">Ready to learn something new today?</p>
                </div>
                <div className="flex gap-2">
                    {user?.roles?.some(role => role === 'ADMIN' || role === 'ROLE_ADMIN') && (
                        <button onClick={() => navigate('/admin')} className="btn-primary bg-slate-800 hover:bg-slate-900">
                            Admin Panel
                        </button>
                    )}
                    
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                
                <div className="card">
                    <h2 className="text-xl font-bold mb-4 text-slate-800">Start a Quiz</h2>
                    <div className="space-y-4">
                        <button
                            onClick={() => navigate('/quiz/setup/adaptive')}
                            className="w-full bg-gradient-to-r from-violet-600 to-indigo-600 text-white p-4 rounded-xl shadow-lg hover:shadow-xl transition-all text-left group"
                        >
                            <div className="font-bold text-lg">Adaptive Mode 🚀</div>
                            <div className="text-indigo-100 text-sm opacity-90">AI-powered difficulty that adjusts to you</div>
                        </button>
                        <button
                            onClick={() => navigate('/quiz/setup/custom')}
                            className="w-full bg-white border-2 border-slate-200 p-4 rounded-xl hover:border-primary-500 transition-all text-left"
                        >
                            <div className="font-bold text-lg text-slate-800">Custom Quiz</div>
                            <div className="text-slate-500 text-sm">Choose subject, difficulty, and timelimit</div>
                        </button>
                        <button
                            onClick={() => navigate('/contests')}
                            className="w-full bg-slate-800 text-white p-4 rounded-xl shadow-lg hover:shadow-xl transition-all text-left group border border-slate-700 hover:bg-slate-900"
                        >
                            <div className="font-bold text-lg">Exam Contests 🏆</div>
                            <div className="text-slate-300 text-sm opacity-90">Compete in time-bound exams</div>
                        </button>
                    </div>
                </div>

                
                <div className="grid grid-cols-1 gap-6">
                    <div className="card">
                        <h2 className="text-xl font-bold mb-4 text-slate-800">Subject Performance</h2>
                        {subjectData.length > 0 ? (
                            <div className="h-64">
                                <ResponsiveContainer width="100%" height="100%">
                                    <BarChart data={subjectData}>
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="name" />
                                        <YAxis domain={[0, 100]} />
                                        <Tooltip />
                                        <Bar dataKey="accuracy" fill="#4f46e5" name="Accuracy %" radius={[4, 4, 0, 0]} />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        ) : (
                            <div className="h-32 flex items-center justify-center text-slate-400">No data yet</div>
                        )}
                    </div>

                    {stats?.quizHistory && stats.quizHistory.length > 0 && (
                        <div className="card">
                            <h2 className="text-xl font-bold mb-4 text-slate-800">Progress Trend (Last 10 Quizzes)</h2>
                            <div className="h-64">
                                <ResponsiveContainer width="100%" height="100%">
                                    <LineChart data={stats.quizHistory}>
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="date" tickFormatter={(d) => new Date(d).toLocaleDateString()} />
                                        <YAxis domain={[0, 100]} />
                                        <Tooltip labelFormatter={(d) => new Date(d).toLocaleString()} />
                                        <Legend />
                                        <Line type="monotone" dataKey="score" stroke="#0ea5e9" strokeWidth={2} name="Score %" />
                                    </LineChart>
                                </ResponsiveContainer>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            
        </div>
    );
}
