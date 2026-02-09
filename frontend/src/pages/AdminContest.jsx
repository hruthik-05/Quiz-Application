import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';

export default function AdminContest() {
    const navigate = useNavigate();
    const { user } = useAuth();
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        startTime: '',
        endTime: '',
        durationMinutes: 30,
        negativeMarking: false,
        negativeMarkFactor: 0.25,
        subject: 'Java',
        difficulty: 'MEDIUM',
        questionCount: 10,
        maxAttempts: 1
    });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const isAdmin = user?.roles?.some(role => role === 'ADMIN' || role === 'ROLE_ADMIN');
        if (user && !isAdmin) {
            navigate('/dashboard');
        }
    }, [user, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {


            const qRes = await api.get(`/quiz/custom/${formData.subject}/${formData.difficulty}/${formData.questionCount}`);
            const questionIds = qRes.data.map(q => q.id);


            const contestPayload = {
                title: formData.title,
                description: formData.description,
                startTime: new Date(formData.startTime).getTime(),
                endTime: new Date(formData.endTime).getTime(),
                durationMinutes: Number(formData.durationMinutes),
                isActive: true, 
                allowNegativeMarking: formData.negativeMarking,
                negativeMarkFactor: Number(formData.negativeMarkFactor),
                questionIds: questionIds,
                maxAttempts: Number(formData.maxAttempts)
            };

            await api.post('/contest/create', contestPayload);
            alert('Contest Created Successfully!');
            navigate('/dashboard');
        } catch (err) {
            console.error(err);
            alert('Failed to create contest');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 p-8">
            <h1 className="text-3xl font-bold text-slate-800 mb-8">Create Exam Contest</h1>
            <form onSubmit={handleSubmit} className="max-w-2xl bg-white p-6 rounded-xl shadow-sm space-y-4">
                <div>
                    <label className="block text-slate-700 font-medium mb-1">Contest Title</label>
                    <input type="text" required className="input-field w-full"
                        value={formData.title} onChange={e => setFormData({ ...formData, title: e.target.value })} />
                </div>
                <div>
                    <label className="block text-slate-700 font-medium mb-1">Description</label>
                    <textarea className="input-field w-full"
                        value={formData.description} onChange={e => setFormData({ ...formData, description: e.target.value })} />
                </div>
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-slate-700 font-medium mb-1">Start Time</label>
                        <input type="datetime-local" required className="input-field w-full"
                            value={formData.startTime} onChange={e => setFormData({ ...formData, startTime: e.target.value })} />
                    </div>
                    <div>
                        <label className="block text-slate-700 font-medium mb-1">End Time</label>
                        <input type="datetime-local" required className="input-field w-full"
                            value={formData.endTime} onChange={e => setFormData({ ...formData, endTime: e.target.value })} />
                    </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-slate-700 font-medium mb-1">Duration (Minutes)</label>
                        <input type="number" required className="input-field w-full"
                            value={formData.durationMinutes} onChange={e => setFormData({ ...formData, durationMinutes: e.target.value })} />
                    </div>
                    <div className="flex items-center pt-8">
                        <input type="checkbox" id="neg" className="w-5 h-5 accent-primary-600 mr-2"
                            checked={formData.negativeMarking} onChange={e => setFormData({ ...formData, negativeMarking: e.target.checked })} />
                        <label htmlFor="neg" className="text-slate-700">Enable Negative Marking (0.25)</label>
                    </div>
                    <div>
                        <label className="block text-slate-700 font-medium mb-1">Max Attempts</label>
                        <input type="number" min="1" required className="input-field w-full"
                            value={formData.maxAttempts} onChange={e => setFormData({ ...formData, maxAttempts: e.target.value })} />
                    </div>
                </div>

                <h3 className="text-lg font-bold text-slate-800 pt-4 border-t">Question Configuration</h3>
                <div className="grid grid-cols-3 gap-4">
                    <div>
                        <label className="block text-slate-700 font-medium mb-1">Subject</label>
                        <select className="input-field w-full"
                            value={formData.subject} onChange={e => setFormData({ ...formData, subject: e.target.value })}>
                            <option value="Java">Java</option>
                            <option value="Python">Python</option>
                            <option value="Spring">Spring</option>
                            <option value="React">React</option>
                            <option value="DSA">DSA</option>
                        </select>
                    </div>
                    <div>
                        <label className="block text-slate-700 font-medium mb-1">Difficulty</label>
                        <select className="input-field w-full"
                            value={formData.difficulty} onChange={e => setFormData({ ...formData, difficulty: e.target.value })}>
                            <option value="EASY">Easy</option>
                            <option value="MEDIUM">Medium</option>
                            <option value="HARD">Hard</option>
                            <option value="MIXED">Mixed</option>
                        </select>
                    </div>
                    <div>
                        <label className="block text-slate-700 font-medium mb-1">Count</label>
                        <input type="number" className="input-field w-full"
                            value={formData.questionCount} onChange={e => setFormData({ ...formData, questionCount: e.target.value })} />
                    </div>
                </div>

                <div className="pt-4 flex justify-end">
                    <button type="submit" disabled={loading} className="btn-primary">
                        {loading ? 'Creating...' : 'Create Contest'}
                    </button>
                </div>
            </form>
        </div>
    );
}
