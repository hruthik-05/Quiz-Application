import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';

export default function ContestPlayer() {
    const { id } = useParams();
    const { user } = useAuth();
    const navigate = useNavigate();

    const [contest, setContest] = useState(null);
    const [questions, setQuestions] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [answers, setAnswers] = useState({});
    const [timeLeft, setTimeLeft] = useState(0);
    const [loading, setLoading] = useState(true);

    const submitContest = async (auto = false) => {
        try {
            const payload = {
                contestId: id,
                userId: user.id || user.userId, 
                responses: answers,
                timeTaken: (contest.durationMinutes * 60 - timeLeft) * 1000 
            };
            const res = await api.post('/contest/submit', payload);
            if (auto) alert("Time's up! Submitting automatically.");


            navigate('/quiz/result', {
                state: {
                    result: {
                        score: res.data.score,
                        totalQuestions: questions.length,
                        classification: "Contest Result",


                    },
                    mode: "CONTEST",
                    total: questions.length
                }
            });
        } catch (err) {
            console.error("Submission failed", err);
            alert("Submission failed: " + (err.response?.data || err.message));
        }
    };


    useEffect(() => {
        const loadContest = async () => {
            try {
                const cRes = await api.get(`/contest/${id}`);
                const c = cRes.data;
                setContest(c);
                setTimeLeft(c.durationMinutes * 60);

                const qRes = await api.get(`/contest/${id}/questions`);
                setQuestions(qRes.data);

            } catch (err) {
                console.error(err);
                alert("Failed to load contest. " + err.message);
                navigate('/dashboard');
            } finally {
                setLoading(false);
            }
        };
        loadContest();
    }, [id, navigate]);


    useEffect(() => {
        if (!loading && timeLeft > 0) {
            const timer = setInterval(() => {
                setTimeLeft(prev => {
                    if (prev <= 1) {
                        clearInterval(timer);
                        submitContest(true); 
                        return 0;
                    }
                    return prev - 1;
                });
            }, 1000);
            return () => clearInterval(timer);
        }

    }, [loading, timeLeft]);

    if (loading) return <div>Loading Exam...</div>;

    const currentQ = questions[currentIndex];


    const formatTime = (s) => {
        const m = Math.floor(s / 60);
        const sec = s % 60;
        return `${m}:${sec < 10 ? '0' : ''}${sec}`;
    };

    return (
        <div className="min-h-screen bg-white flex flex-col">
            
            <div className="bg-slate-900 text-white px-8 py-4 flex justify-between items-center sticky top-0 z-50">
                <div>
                    <h1 className="text-xl font-bold">{contest.title}</h1>
                    <div className="text-sm text-slate-400">Question {currentIndex + 1} / {questions.length}</div>
                </div>
                <div className={`text-2xl font-mono font-bold ${timeLeft < 60 ? 'text-red-500 animate-pulse' : ''}`}>
                    {formatTime(timeLeft)}
                </div>
            </div>

            
            <div className="flex-1 flex justify-center p-8">
                <div className="max-w-4xl w-full">
                    <p className="text-xl font-medium mb-8 leading-relaxed text-slate-800">
                        {currentQ?.questionText || currentQ?.question}
                    </p>
                    <div className="space-y-3">
                        {currentQ?.options?.map((opt, idx) => (
                            <button key={idx}
                                onClick={() => setAnswers({ ...answers, [currentQ.id]: opt })}
                                className={`w-full text-left p-4 border rounded-xl transition-all ${answers[currentQ.id] === opt
                                    ? 'bg-blue-50 border-blue-500 text-blue-900 font-medium'
                                    : 'hover:bg-gray-50'
                                    }`}
                            >
                                <span className="mr-4 font-bold text-slate-400">{String.fromCharCode(65 + idx)}</span>
                                {opt}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            
            <div className="border-t p-4 flex justify-between bg-slate-50">
                <div className="space-x-4">
                    
                </div>
                <div className="space-x-4">
                    <button className="btn-secondary"
                        disabled={currentIndex === 0}
                        onClick={() => setCurrentIndex(c => c - 1)}>Prev</button>
                    {currentIndex < questions.length - 1 ? (
                        <button className="btn-primary" onClick={() => setCurrentIndex(c => c + 1)}>Next</button>
                    ) : (
                        <button className="bg-green-600 text-white px-6 py-2 rounded-lg font-bold hover:bg-green-700"
                            onClick={() => submitContest(false)}>Submit Exam</button>
                    )}
                </div>
            </div>
        </div>
    );
}
