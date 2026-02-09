import { useState, useEffect } from 'react';
import api from '../services/api';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    ArcElement
} from 'chart.js';
import { Bar, Pie } from 'react-chartjs-2';

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    ArcElement
);

export default function AdminAnalytics() {
    const [userStats, setUserStats] = useState([]);
    const [contestAttempts, setContestAttempts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [uRes, cRes] = await Promise.all([
                    api.get('/analytics/admin/all-user-stats'),
                    api.get('/analytics/admin/all-contest-attempts')
                ]);
                setUserStats(uRes.data);
                setContestAttempts(cRes.data);
            } catch (err) {
                console.error("Failed to fetch analytics", err);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);


    const subjectCounts = {};
    userStats.forEach(stat => {
        if (stat.subjectAttempts) {
            Object.entries(stat.subjectAttempts).forEach(([subj, count]) => {
                subjectCounts[subj] = (subjectCounts[subj] || 0) + count;
            });
        }
    });

    const barData = {
        labels: Object.keys(subjectCounts),
        datasets: [{
            label: 'Total Quiz Attempts',
            data: Object.values(subjectCounts),
            backgroundColor: 'rgba(99, 102, 241, 0.6)',
        }]
    };


    const accuracies = userStats.map(u => {
        if (!u.subjectAccuracy) return 0;
        const vals = Object.values(u.subjectAccuracy);
        if (vals.length === 0) return 0;
        return vals.reduce((a, b) => a + b, 0) / vals.length;
    }).filter(a => a > 0);


    let accBins = { '90+': 0, '70-90': 0, '50-70': 0, '<50': 0 };
    accuracies.forEach(a => {
        if (a >= 90) accBins['90+']++;
        else if (a >= 70) accBins['70-90']++;
        else if (a >= 50) accBins['50-70']++;
        else accBins['<50']++;
    });

    const pieData = {
        labels: Object.keys(accBins),
        datasets: [{
            data: Object.values(accBins),
            backgroundColor: [
                'rgba(16, 185, 129, 0.6)', 
                'rgba(59, 130, 246, 0.6)', 
                'rgba(245, 158, 11, 0.6)', 
                'rgba(239, 68, 68, 0.6)', 
            ]
        }]
    };


    const leaderboard = userStats.map(u => {
        const totalAttempts = u.subjectAttempts ? Object.values(u.subjectAttempts).reduce((a, b) => a + b, 0) : 0;
        const subAccs = u.subjectAccuracy ? Object.values(u.subjectAccuracy) : [];
        const avgAcc = subAccs.length ? (subAccs.reduce((a, b) => a + b, 0) / subAccs.length) : 0;
        return { userId: u.userId, username: u.username || u.userId, totalAttempts, avgAcc };
    })
        .sort((a, b) => b.avgAcc - a.avgAcc)
        .slice(0, 5);

    if (loading) return <div className="p-8">Loading Analytics...</div>;

    return (
        <div className="p-8 max-w-7xl mx-auto bg-slate-50 min-h-screen">
            <h1 className="text-3xl font-bold text-slate-800 mb-8">Global Analytics Dashboard</h1>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
                
                <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100">
                    <h2 className="text-xl font-bold mb-4 text-slate-700">Subject Popularity</h2>
                    <Bar data={barData} options={{ responsive: true }} />
                </div>

                
                <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100">
                    <h2 className="text-xl font-bold mb-4 text-slate-700">Student Performance Distribution</h2>
                    <div className="h-64 flex justify-center">
                        <Pie data={pieData} />
                    </div>
                </div>
            </div>

            
            <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100 mb-8">
                <h2 className="text-xl font-bold mb-4 text-slate-700 text-indigo-600">Top Performers (By Accuracy)</h2>
                <table className="w-full text-left">
                    <thead className="border-b border-slate-200 bg-slate-50 text-slate-600 font-medium">
                        <tr>
                            <th className="p-3">Rank</th>
                            <th className="p-3">User ID</th>
                            <th className="p-3">Avg Accuracy</th>
                            <th className="p-3">Total Attempts</th>
                        </tr>
                    </thead>
                    <tbody>
                        {leaderboard.map((user, idx) => (
                            <tr key={user.userId} className="border-b border-slate-50 hover:bg-slate-50">
                                <td className="p-3 font-bold text-slate-500">#{idx + 1}</td>
                                <td className="p-3 font-medium text-slate-900">
                                    {user.username}
                                    <span className="text-xs text-slate-400 ml-2 block sm:inline">({user.userId})</span>
                                </td>
                                <td className="p-3 text-emerald-600 font-bold">{user.avgAcc.toFixed(1)}%</td>
                                <td className="p-3 text-slate-600">{user.totalAttempts}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            
            <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100">
                <h2 className="text-xl font-bold mb-4 text-slate-700">Recent Contest Activity</h2>
                <div className="overflow-auto max-h-60">
                    <table className="w-full text-left text-sm">
                        <thead className="bg-slate-50 border-b border-slate-200">
                            <tr>
                                <th className="p-3">Contest ID</th>
                                <th className="p-3">User ID</th>
                                <th className="p-3">Score</th>
                                <th className="p-3">Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            {contestAttempts.slice(-10).reverse().map(a => (
                                <tr key={a.id} className="border-b border-slate-50">
                                    <td className="p-3 font-mono text-xs">{a.contestId}</td>
                                    <td className="p-3">{a.userId}</td>
                                    <td className="p-3 font-bold text-indigo-600">{a.score}</td>
                                    <td className="p-3 text-slate-500">{new Date(a.submittedAt).toLocaleDateString()}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}
