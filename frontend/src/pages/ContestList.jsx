import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';

export default function ContestList() {
    const [contests, setContests] = useState([]);
    const [myAttempts, setMyAttempts] = useState({});
    const navigate = useNavigate();
    const { user } = useAuth();

    useEffect(() => {
        fetchData();

    }, []);

    const fetchData = async () => {
        try {
            const cRes = await api.get('/contest/all');
            setContests(cRes.data);


            if (user) {
                const aRes = await api.get(`/contest/my-results/${user.id}`);

                const attemptsMap = {};
                aRes.data.forEach(a => {
                    attemptsMap[a.contestId] = a;
                });
                setMyAttempts(attemptsMap);
            }
        } catch (err) {
            console.error(err);
        }
    };

    const handleAction = (contest) => {
        const now = Date.now();
        const attempt = myAttempts[contest.id];


        if (attempt) {
            navigate('/quiz/result', {
                state: {
                    result: {
                        score: attempt.score,
                        totalQuestions: Object.keys(attempt.responses).length, 
                        correct: null, 
                        classification: "Contest Result"
                    },
                    mode: "CONTEST"
                }
            });
            return;
        }


        if (now < contest.startTime) {
            alert('Contest has not started yet!');
            return;
        }


        if (now > contest.endTime) {
            alert('Contest has ended!');
            return;
        }


        if (window.confirm("Are you ready to start? The timer will begin immediately.")) {
            navigate(`/contest/${contest.id}`);
        }
    };

    const getButtonState = (contest) => {
        const now = Date.now();
        const attempt = myAttempts[contest.id];

        if (attempt) return { label: "View Result", style: "bg-emerald-600 hover:bg-emerald-700 text-white" };
        if (now > contest.endTime) return { label: "Ended", style: "bg-slate-300 text-slate-500 cursor-not-allowed", disabled: true };
        if (now < contest.startTime) return { label: "Upcoming", style: "bg-amber-100 text-amber-800" };
        return { label: "Enter Exam", style: "btn-primary" };
    };

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold mb-6">Exams & Contests</h1>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {contests.map(c => {
                    const btn = getButtonState(c);
                    return (
                        <div key={c.id} className="card hover:shadow-md transition-shadow">
                            <h2 className="text-xl font-bold text-slate-800">{c.title}</h2>
                            <p className="text-slate-600 mt-2">{c.description}</p>
                            <div className="mt-4 flex gap-4 text-sm text-slate-500 font-medium pb-4 border-b border-slate-100 mb-4">
                                <span>⏱ {c.durationMinutes} mins</span>
                                <span>📅 {new Date(c.startTime).toLocaleString()}</span>
                            </div>
                            <button
                                onClick={() => handleAction(c)}
                                disabled={btn.disabled}
                                className={`w-full py-2.5 rounded-lg font-medium transition ${btn.style}`}
                            >
                                {btn.label}
                            </button>
                        </div>
                    );
                })}
                {contests.length === 0 && <p className="text-slate-500">No contests available.</p>}
            </div>
        </div>
    );
}
