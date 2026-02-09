import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

export default function QuizSetup() {
    const { mode } = useParams(); 
    const navigate = useNavigate();

    const [subject, setSubject] = useState('Java');
    const [difficulty, setDifficulty] = useState('MEDIUM');
    const [count, setCount] = useState(10);
    const [loading, setLoading] = useState(false);

    const subjects = ['Java', 'Python', 'DBMS', 'OS', 'CN']; 

    const handleStart = async () => {
        setLoading(true);


        const config = {
            mode, 
            subject,
            difficulty: mode === 'adaptive' ? null : difficulty,
            count
        };


        navigate('/quiz/play', { state: config });
    };

    return (
        <div className="min-h-screen bg-slate-50 p-8 flex items-center justify-center">
            <div className="card max-w-lg w-full">
                <h1 className="text-2xl font-bold mb-2 capitalize text-primary-900">{mode} Quiz Setup</h1>
                <p className="text-slate-500 mb-6">Configure your session parameters</p>

                <div className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium mb-2">Subject</label>
                        <div className="grid grid-cols-2 gap-2">
                            {subjects.map(s => (
                                <button
                                    key={s}
                                    onClick={() => setSubject(s)}
                                    className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${subject === s
                                        ? 'bg-primary-600 text-white shadow-md'
                                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                                        }`}
                                >
                                    {s}
                                </button>
                            ))}
                        </div>
                    </div>

                    {mode === 'custom' && (
                        <div>
                            <label className="block text-sm font-medium mb-2">Difficulty</label>
                            <div className="flex gap-2">
                                {['EASY', 'MEDIUM', 'HARD', 'MIXED'].map(d => (
                                    <button
                                        key={d}
                                        onClick={() => setDifficulty(d)}
                                        className={`flex-1 px-4 py-2 rounded-lg text-sm font-medium transition-colors ${difficulty === d
                                            ? 'bg-slate-800 text-white'
                                            : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                                            }`}
                                    >
                                        {d}
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium mb-2">Number of Questions: {count}</label>
                        <input
                            type="range"
                            min="5"
                            max="20"
                            step="1"
                            value={count}
                            onChange={(e) => setCount(parseInt(e.target.value))}
                            className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-primary-600"
                        />
                        <div className="flex justify-between text-xs text-slate-400 mt-1">
                            <span>5</span>
                            <span>20</span>
                        </div>
                    </div>

                    <button
                        onClick={handleStart}
                        disabled={loading}
                        className="btn-primary w-full py-3 text-lg shadow-lg hover:shadow-xl hover:-translate-y-0.5 transform transition-all"
                    >
                        Start Quiz
                    </button>

                    <button onClick={() => navigate('/dashboard')} className="w-full text-slate-500 text-sm hover:underline">Cancel</button>
                </div>
            </div>
        </div>
    );
}
