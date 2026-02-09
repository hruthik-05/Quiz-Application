import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function AdminContestManager() {
    const navigate = useNavigate();
    const [contests, setContests] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchContests();
    }, []);

    const fetchContests = async () => {
        try {
            const res = await api.get('/contest/all');
            setContests(res.data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-8 max-w-7xl mx-auto min-h-screen bg-slate-50">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold text-slate-800">Manage Contests</h1>
                <button
                    onClick={() => navigate('/admin/contest')}
                    className="btn-primary"
                >
                    + Create New Contest
                </button>
            </div>

            {loading ? <div>Loading...</div> : (
                <div className="grid gap-4">
                    {contests.map(contest => (
                        <div key={contest.id} className="bg-white p-6 rounded-lg shadow-sm border border-slate-200 flex justify-between items-center group hover:border-indigo-200 transition">
                            <div>
                                <h3 className="text-lg font-bold text-slate-800">{contest.title}</h3>
                                <p className="text-sm text-slate-500 mt-1">
                                    {new Date(contest.startTime).toLocaleString()} — {new Date(contest.endTime).toLocaleString()}
                                </p>
                            </div>
                            <div className="flex space-x-3">
                                <button
                                    onClick={() => navigate(`/admin/contest-report/${contest.id}`)}
                                    className="px-4 py-2 bg-indigo-50 text-indigo-700 rounded-lg hover:bg-indigo-100 font-medium text-sm"
                                >
                                    View Report
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
