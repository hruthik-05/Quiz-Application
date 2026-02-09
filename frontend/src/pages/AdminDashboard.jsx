import { useState, useEffect } from 'react';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function AdminDashboard() {
    const { user } = useAuth();
    const navigate = useNavigate();

    const [questions, setQuestions] = useState([]);
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [newQuestion, setNewQuestion] = useState({
        questionText: '',
        options: ['', '', '', ''],
        answer: '',
        category: 'Java',
        difficulty: 'MEDIUM',
        points: 5
    });

    useEffect(() => {
        console.log("USER:", user);
        console.log("ROLES:", user?.roles);



        const isAdmin = user?.roles?.some(role => role === 'ADMIN' || role === 'ROLE_ADMIN');

        if (user && !isAdmin) {
            navigate('/dashboard');
            return;
        }
        if (user) fetchQuestions();

    }, [user]);

    const fetchQuestions = async () => {
        try {
            const res = await api.get('/admin/questions');
            setQuestions(res.data);
        } catch (err) {
            console.error("Failed to fetch questions", err);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this question?")) return;
        try {
            await api.delete(`/admin/questions/${id}`);
            setQuestions(questions.filter(q => q.id !== id));
        } catch (err) {
            console.error("Failed to delete", err);
            alert("Failed to delete question");
        }
    };

    const handleAdd = async (e) => {
        e.preventDefault();
        try {
            const res = await api.post('/admin/questions', newQuestion);
            setQuestions([...questions, res.data]);
            setNewQuestion({
                questionText: '',
                options: ['', '', '', ''],
                answer: '',
                category: 'Java',
                difficulty: 'MEDIUM',
                points: 5
            });
            alert("Question added!");
        } catch (err) {
            console.error("Failed to add", err);
            alert("Failed to add question");
        }
    };

    return (
        <div className="p-8 max-w-7xl mx-auto min-h-screen bg-slate-50">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold text-slate-800">Admin Dashboard</h1>
                <button
                    onClick={() => navigate('/admin/contest')}
                    className="btn-primary bg-indigo-600 hover:bg-indigo-700"
                >
                    Create New Contest
                </button>
            </div>
            
            {
                stats && (
                    <div className="grid grid-cols-4 gap-6 mb-8">
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100">
                            <div className="text-slate-500 text-sm font-medium">Total Users</div>
                            <div className="text-3xl font-bold text-slate-800 mt-2">{stats.totalUsers}</div>
                        </div>
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100">
                            <div className="text-slate-500 text-sm font-medium">Active Contests</div>
                            <div className="text-3xl font-bold text-indigo-600 mt-2">{stats.activeContests}</div>
                        </div>
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100">
                            <div className="text-slate-500 text-sm font-medium">Questions Bank</div>
                            <div className="text-3xl font-bold text-emerald-600 mt-2">{stats.totalQuestions}</div>
                        </div>
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100">
                            <div className="text-slate-500 text-sm font-medium">Total Attempts</div>
                            <div className="text-3xl font-bold text-amber-600 mt-2">{stats.totalAttempts}</div>
                        </div>
                    </div>
                )
            }

            
            <div className="card mb-8">
                <h2 className="text-xl font-bold mb-4">Add New Question</h2>
                <form onSubmit={handleAdd} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium">Question Text</label>
                        <input
                            required
                            className="input-field"
                            value={newQuestion.questionText}
                            onChange={(e) => setNewQuestion({ ...newQuestion, questionText: e.target.value })}
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        {newQuestion.options.map((opt, i) => (
                            <div key={i}>
                                <label className="block text-sm font-medium">Option {i + 1}</label>
                                <input
                                    required
                                    className="input-field"
                                    value={opt}
                                    onChange={(e) => {
                                        const newOpts = [...newQuestion.options];
                                        newOpts[i] = e.target.value;
                                        setNewQuestion({ ...newQuestion, options: newOpts });
                                    }}
                                />
                            </div>
                        ))}
                    </div>

                    <div className="grid grid-cols-3 gap-4">
                        <div>
                            <label className="block text-sm font-medium">Correct Answer (Must match option)</label>
                            <input
                                required
                                className="input-field"
                                value={newQuestion.answer}
                                onChange={(e) => setNewQuestion({ ...newQuestion, answer: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium">Subject</label>
                            <select
                                className="input-field"
                                value={newQuestion.category}
                                onChange={(e) => setNewQuestion({ ...newQuestion, category: e.target.value })}
                            >
                                {['Java', 'Python', 'DBMS', 'OS', 'CN', 'DSA'].map(s => <option key={s} value={s}>{s}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium">Difficulty</label>
                            <select
                                className="input-field"
                                value={newQuestion.difficulty}
                                onChange={(e) => setNewQuestion({ ...newQuestion, difficulty: e.target.value })}
                            >
                                <option value="EASY">EASY</option>
                                <option value="MEDIUM">MEDIUM</option>
                                <option value="HARD">HARD</option>
                            </select>
                        </div>
                    </div>

                    <button type="submit" className="btn-primary w-full">Add Question</button>
                </form>
            </div>

            
            <div className="card">
                <h2 className="text-xl font-bold mb-4">Question Bank ({questions.length})</h2>
                {loading ? <p>Loading...</p> : (
                    <div className="space-y-4 max-h-[600px] overflow-y-auto">
                        {questions.map(q => (
                            <div key={q.id} className="p-4 border rounded-lg flex justify-between items-start hover:bg-slate-50">
                                <div>
                                    <div className="font-medium text-lg">{q.questionText}</div>
                                    <div className="text-sm text-slate-500 mt-1">
                                        <span className="bg-blue-100 text-blue-800 px-2 py-0.5 rounded text-xs mr-2">{q.category}</span>
                                        <span className={`px-2 py-0.5 rounded text-xs mr-2 ${q.difficulty === 'HARD' ? 'bg-red-100 text-red-800' :
                                            q.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' :
                                                'bg-green-100 text-green-800'
                                            }`}>{q.difficulty}</span>
                                        <span>Ans: {q.answer}</span>
                                    </div>
                                </div>
                                <button
                                    onClick={() => handleDelete(q.id)}
                                    className="text-red-500 hover:text-red-700 text-sm font-medium"
                                >
                                    Delete
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div >
    );
}
