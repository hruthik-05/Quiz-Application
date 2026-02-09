import { useLocation, useNavigate } from 'react-router-dom';

export default function Result() {
    const { state } = useLocation();
    const navigate = useNavigate();

    if (!state?.result) {
        return <div className="p-8">No Result Data</div>;
    }

    const { result, total, mode } = state;



    const isContest = mode === 'CONTEST';
    const displayScore = result.score || 0;

    const totalQ = total || result.totalQuestions || 0;




    const percentage = totalQ > 0 ? Math.round((displayScore / totalQ) * 100) : 0; 

    let feedback = "Result Summary";
    if (percentage >= 90) feedback = "Outstanding Performance! 🏆";
    else if (percentage >= 70) feedback = "Great Job! 🌟";
    else if (percentage >= 50) feedback = "Well Done! 👍";
    else feedback = "Keep Practicing! 💪";

    return (
        <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
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
        </div>
    );
}
