import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function Result() {
    const { state } = useLocation();
    const navigate = useNavigate();
    const [questions, setQuestions] = useState(state?.questions || []);
    const [loadingQuestions, setLoadingQuestions] = useState(false);

    if (!state?.result) {
        return <div className="p-8">No Result Data</div>;
    }

    const { result, total, mode, attempt } = state;



    const isContest = mode === 'CONTEST';
    const isPractice = mode === 'PRACTICE';
    const displayScore = result.score || 0;

    const totalQ = total || result.totalQuestions || 0;

    useEffect(() => {
        if (isContest && attempt && (!state?.questions || state.questions.length === 0)) {
            setLoadingQuestions(true);
            api.get(`/contest/${attempt.contestId}/questions`)
                .then(res => {
                    setQuestions(res.data);
                })
                .catch(err => {
                    console.error("Failed to load contest questions:", err);
                })
                .finally(() => {
                    setLoadingQuestions(false);
                });
        } else if (isPractice && state?.responses && Object.keys(state.responses).length > 0) {
            setLoadingQuestions(true);
            const questionIds = Object.keys(state.responses);
            api.post('/question/getQuestionsByListOfIds', questionIds)
                .then(res => {
                    setQuestions(res.data);
                })
                .catch(err => {
                    console.error("Failed to load practice questions:", err);
                })
                .finally(() => {
                    setLoadingQuestions(false);
                });
        }
    }, [isContest, isPractice, attempt, state?.questions, state?.responses]);




    const percentage = totalQ > 0 ? Math.round((displayScore / totalQ) * 100) : 0; 

    let feedback = "Result Summary";
    if (percentage >= 90) feedback = "Outstanding Performance! 🏆";
    else if (percentage >= 70) feedback = "Great Job! 🌟";
    else if (percentage >= 50) feedback = "Well Done! 👍";
    else feedback = "Keep Practicing! 💪";

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4 py-12">
            <div className="card max-w-2xl w-full text-center p-12">
                <div className="mb-8">
                    <h1 className="text-4xl font-extrabold text-slate-900 mb-2">{feedback}</h1>
                    <p className="text-slate-500">You completed the quiz successfully</p>
                </div>

                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-12">
                    
                    {result.correct !== undefined && !isContest && (
                        <>
                            <div className="p-4 bg-emerald-50 rounded-xl border border-emerald-100">
                                <div className="text-3xl font-bold text-emerald-600">{result.correct}</div>
                                <div className="text-sm text-emerald-800 font-medium">Correct</div>
                            </div>
                            <div className="p-4 bg-rose-50 rounded-xl border border-rose-100">
                                <div className="text-3xl font-bold text-rose-600">{result.wrong}</div>
                                <div className="text-sm text-rose-800 font-medium">Wrong</div>
                            </div>
                        </>
                    )}

                    
                    {isContest && (
                        <div className="p-4 bg-purple-50 rounded-xl border border-purple-100 col-span-2">
                            <div className="text-3xl font-bold text-purple-600">{result.score}</div>
                            <div className="text-sm text-purple-800 font-medium">Total Score</div>
                        </div>
                    )}

                    {!isContest && (
                        <div className="p-4 bg-amber-50 rounded-xl border border-amber-100">
                            <div className="text-3xl font-bold text-amber-600">{result.skipped || 0}</div>
                            <div className="text-sm text-amber-800 font-medium">Skipped</div>
                        </div>
                    )}

                    <div className="p-4 bg-indigo-50 rounded-xl border border-indigo-100">
                        
                        <div className="text-3xl font-bold text-indigo-600">{isContest ? 'N/A' : percentage + '%'}</div>
                        <div className="text-sm text-indigo-800 font-medium">Accuracy</div>
                    </div>
                </div>

                <div className="flex justify-center gap-4">
                    <button onClick={() => navigate('/dashboard')} className="btn-secondary">
                        Back to Dashboard
                    </button>
                    <button onClick={() => navigate('/quiz/setup/adaptive')} className="btn-primary">
                        Try Another Quiz
                    </button>
                </div>
            </div>

            {(isContest || isPractice) && (
                <div className="mt-8 max-w-3xl w-full text-left">
                    <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 md:p-8">
                        <h2 className="text-2xl font-bold text-slate-900 mb-6 flex items-center gap-2">
                            <span>📝</span> Question & Response Review
                        </h2>
                        {loadingQuestions ? (
                            <div className="flex items-center justify-center py-12">
                                <div className="text-slate-400 animate-pulse font-medium">Loading questions review...</div>
                            </div>
                        ) : questions.length === 0 ? (
                            <div className="text-slate-400 text-center py-8">No question details available.</div>
                        ) : (
                            <div className="space-y-6">
                                {questions.map((q, idx) => {
                                    const userResp = isContest ? attempt?.responses?.[q.id] : state?.responses?.[q.id];
                                    const isCorrect = q.answer && userResp && q.answer.trim().toLowerCase() === userResp.trim().toLowerCase();
                                    return (
                                        <div key={q.id} className="pb-6 border-b border-slate-100 last:border-0 last:pb-0">
                                            <div className="flex justify-between items-start gap-4 mb-3">
                                                <div className="flex items-center gap-2">
                                                    <span className="font-semibold text-slate-500 text-sm">Question {idx + 1}</span>
                                                    <span className="text-xs font-semibold px-2 py-0.5 rounded bg-slate-100 text-slate-600 border border-slate-200">{q.points} pts</span>
                                                </div>
                                                {userResp ? (
                                                    isCorrect ? (
                                                        <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">Correct</span>
                                                    ) : (
                                                        <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-rose-50 text-rose-700 border border-rose-200">Incorrect</span>
                                                    )
                                                ) : (
                                                    <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-amber-50 text-amber-700 border border-amber-200">Unanswered</span>
                                                )}
                                            </div>
                                            <p className="text-slate-800 font-medium mb-4">{q.questionText || q.question}</p>
                                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                                {q.options?.map((opt, oIdx) => {
                                                    const isSelected = userResp === opt;
                                                    const isCorrectOpt = q.answer === opt;
                                                    let optClass = "border border-slate-200 text-slate-600 bg-slate-50/50 hover:bg-slate-50";
                                                    if (isSelected) {
                                                        optClass = isCorrect 
                                                            ? "border-emerald-500 bg-emerald-50 text-emerald-900 font-medium" 
                                                            : "border-rose-500 bg-rose-50 text-rose-900 font-medium";
                                                    } else if (isCorrectOpt) {
                                                        optClass = "border-emerald-500/50 bg-emerald-50/20 text-emerald-800";
                                                    }
                                                    return (
                                                        <div key={oIdx} className={`p-3.5 rounded-xl border transition flex items-start gap-2.5 text-sm ${optClass}`}>
                                                            <span className="font-bold text-slate-400">{String.fromCharCode(65 + oIdx)}.</span>
                                                            <span className="flex-1">{opt}</span>
                                                        </div>
                                                    );
                                                })}
                                            </div>
                                            {!isCorrect && q.answer && (
                                                <div className="mt-3 text-xs text-slate-500 bg-slate-50 p-2.5 rounded-lg border border-slate-100 flex items-center gap-1.5">
                                                    <span className="font-bold text-slate-700">Correct Answer:</span>
                                                    <span className="text-slate-600">{q.answer}</span>
                                                </div>
                                            )}
                                        </div>
                                    );
                                })}
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}
