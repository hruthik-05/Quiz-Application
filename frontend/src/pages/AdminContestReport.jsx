import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function AdminContestReport() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [attempts, setAttempts] = useState([]);
    const [contest, setContest] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchData();

    }, [id]);

    const fetchData = async () => {
        try {
            const [cRes, aRes] = await Promise.all([
                api.get(`/contest/${id}`),
                api.get(`/contest/${id}/attempts`)
            ]);
            setContest(cRes.data);
            setAttempts(aRes.data);
        } catch (err) {
            console.error(err);
            alert("Failed to load report");
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div className="p-8">Loading Report...</div>;

    return (
        <div className="p-8 max-w-7xl mx-auto min-h-screen bg-slate-50">
            <button onClick={() => navigate('/admin/contest-manager')} className="mb-4 text-indigo-600 hover:underline">
                &larr; Back to Contests
            </button>

            <div className="flex justify-between items-end mb-6">
                <div>
                    <h1 className="text-3xl font-bold text-slate-800">Contest Report</h1>
                    <p className="text-slate-600 text-lg mt-1">{contest?.title}</p>
                </div>
                <div className="text-right">
                    <div className="text-sm text-slate-500">Total Attempts</div>
                    <div className="text-2xl font-bold text-indigo-600">{attempts.length}</div>
                </div>
            </div>

            <div className="bg-white rounded-lg shadow-sm border border-slate-200 overflow-hidden">
                <table className="w-full text-left text-sm text-slate-600">
                    <thead className="bg-slate-50 border-b border-slate-200 font-medium text-slate-700">
                        <tr>
                            <th className="px-6 py-4">Participant</th>
                            <th className="px-6 py-4">Submitted At</th>
                            <th className="px-6 py-4">Time Taken (s)</th>
                            <th className="px-6 py-4">Score</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        {attempts.map(attempt => (
                            <tr key={attempt.id} className="hover:bg-slate-50">
                                <td className="px-6 py-4">
                                    <div className="font-medium text-slate-900">{attempt.username}</div>
                                    <div className="text-xs text-slate-500">{attempt.email}</div>
                                </td>
                                <td className="px-6 py-4">{new Date(attempt.submittedAt).toLocaleString()}</td>
                                <td className="px-6 py-4">{(attempt.timeTaken / 1000).toFixed(1)}s</td>
                                <td className="px-6 py-4 font-bold text-emerald-600">{attempt.score}</td>
                            </tr>
                        ))}
                        {attempts.length === 0 && (
                            <tr>
                                <td colSpan="4" className="px-6 py-8 text-center text-slate-400">
                                    No attempts recorded yet.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
