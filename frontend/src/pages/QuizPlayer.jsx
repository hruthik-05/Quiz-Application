import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';

export default function QuizPlayer() {
    const { state } = useLocation(); 
    const { user } = useAuth();
    const navigate = useNavigate();

    const [questions, setQuestions] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [answers, setAnswers] = useState({});
    const [loading, setLoading] = useState(true);
    const [startTime] = useState(Date.now());

    useEffect(() => {
        if (!state) {
            navigate('/dashboard');
            return;
        }
        fetchQuestions();

    }, []);

    const fetchQuestions = async () => {
        try {
            let url = '';
            if (state.mode === 'adaptive') {
                url = `/quiz/adaptive/${user.id}/${state.subject}/${state.count}`;
            } else {


                const diff = state.difficulty || 'MIXED';
                url = `/quiz/custom/${state.subject}/${diff}/${state.count}`;
            }

            const res = await api.get(url);


            setQuestions(res.data);
        } catch (err) {
            console.error("Failed to load quiz", err);
            alert("Failed to load questions. Please try again.");
            navigate('/dashboard');
        } finally {
            setLoading(false);
        }
    };

    const handleOptionSelect = (option) => {
        setAnswers({
            ...answers,
            [questions[currentIndex].id]: option
        });
    };

    const handleNext = () => {
        if (currentIndex < questions.length - 1) {
            setCurrentIndex(currentIndex + 1);
        } else {
            submitQuiz();
        }
    };

    const submitQuiz = async () => {
        try {
            setLoading(true);
            const submitPayload = {
                userId: user.id || user.userId,
                subject: state.subject,
                responses: answers,
                startTime,
                endTime: Date.now(),
                timeLimit: 0
            };

            const res = await api.post('/quiz/submit', submitPayload);
            navigate('/quiz/result', { state: { result: res.data, total: questions.length } });
        } catch (err) {
            console.error("Submit failed", err);
            alert("Submission failed: " + (err.response?.data?.message || err.message));
            setLoading(false);
        }
    };

    if (loading) return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50 text-primary-600 font-bold text-xl animate-pulse">
            Loading Quiz...
        </div>
    );

    if (questions.length === 0) return (
        <div className="min-h-screen flex items-center justify-center flex-col bg-slate-50">
            <h2 className="text-xl font-bold mb-4">No questions found</h2>
            <button onClick={() => navigate('/dashboard')} className="btn-primary">Go Back</button>
        </div>
    );

    const currentQ = questions[currentIndex];

    const selectedOption = answers[currentQ.id];

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col">
            
            <div className="bg-white border-b border-slate-200 px-8 py-4 flex justify-between items-center shadow-sm">
                <div className="font-bold text-slate-700">
                    Question {currentIndex + 1} <span className="text-slate-400">/ {questions.length}</span>
                </div>
                <div className="text-sm font-medium bg-indigo-50 text-indigo-700 px-3 py-1 rounded-full uppercase tracking-wider">
                    {state.mode} • {state.subject}
                </div>
            </div>

            
            <div className="flex-1 flex items-center justify-center p-6">
                <div className="max-w-3xl w-full">
                    <div className="text-2xl font-medium text-slate-800 mb-8 leading-relaxed">
                        {currentQ.questionText || currentQ.question} 
                    </div>

                    <div className="space-y-3">
                        {currentQ.options?.map((opt, idx) => (
                            <button
                                key={idx}
                                onClick={() => handleOptionSelect(opt)}
                                className={`w-full text-left p-4 rounded-xl border-2 transition-all flex items-center group ${selectedOption === opt
                                    ? 'border-primary-500 bg-primary-50 text-primary-900 shadow-md'
                                    : 'border-slate-200 hover:border-primary-200 hover:bg-slate-50 text-slate-600'
                                    }`}
                            >
                                <span className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold mr-4 transition-colors ${selectedOption === opt
                                    ? 'bg-primary-500 text-white'
                                    : 'bg-slate-100 text-slate-400 group-hover:bg-primary-100 group-hover:text-primary-600'
                                    }`}>
                                    {String.fromCharCode(65 + idx)}
                                </span>
                                <span className="font-medium text-lg">{opt}</span>
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            
            <div className="bg-white border-t border-slate-200 px-8 py-4 flex justify-end">
                <button
                    onClick={handleNext}
                    disabled={!selectedOption}
                    className="btn-primary px-8 py-3 text-lg shadow-lg hover:shadow-primary-500/25 disabled:opacity-50 disabled:shadow-none"
                >
                    {currentIndex === questions.length - 1 ? 'Finish Quiz' : 'Next Question'}
                </button>
            </div>
        </div>
    );
}
