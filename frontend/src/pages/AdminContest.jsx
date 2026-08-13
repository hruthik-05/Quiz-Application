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
        maxAttempts: 1
    });
    const [contestQuestions, setContestQuestions] = useState([]);
    const [questionForm, setQuestionForm] = useState({
        questionText: '',
        optionA: '',
        optionB: '',
        optionC: '',
        optionD: '',
        answer: 'A',
        points: 5,
        difficulty: 'MEDIUM',
        category: 'Java'
    });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const isAdmin = user?.roles?.some(role => role === 'ADMIN' || role === 'ROLE_ADMIN');
        if (user && !isAdmin) {
            navigate('/dashboard');
        }
    }, [user, navigate]);

    const handleAddQuestion = (e) => {
        e.preventDefault();
        if (!questionForm.questionText.trim()) {
            alert('Question text is required');
            return;
        }
        if (!questionForm.optionA.trim() || !questionForm.optionB.trim() || !questionForm.optionC.trim() || !questionForm.optionD.trim()) {
            alert('All four options are required');
            return;
        }
        
        let correctText = '';
        if (questionForm.answer === 'A') correctText = questionForm.optionA;
        else if (questionForm.answer === 'B') correctText = questionForm.optionB;
        else if (questionForm.answer === 'C') correctText = questionForm.optionC;
        else if (questionForm.answer === 'D') correctText = questionForm.optionD;

        const newQ = {
            questionText: questionForm.questionText,
            options: [questionForm.optionA, questionForm.optionB, questionForm.optionC, questionForm.optionD],
            answer: correctText,
            points: Number(questionForm.points),
            difficulty: questionForm.difficulty,
            category: questionForm.category
        };

        setContestQuestions([...contestQuestions, newQ]);
        setQuestionForm({
            ...questionForm,
            questionText: '',
            optionA: '',
            optionB: '',
            optionC: '',
            optionD: '',
            answer: 'A'
        });
    };

    const handleRemoveQuestion = (idx) => {
        setContestQuestions(contestQuestions.filter((_, i) => i !== idx));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (contestQuestions.length === 0) {
            alert('Please add at least one question to the contest.');
            return;
        }
        setLoading(true);
        try {
            const contestPayload = {
                title: formData.title,
                description: formData.description,
                startTime: new Date(formData.startTime).getTime(),
                endTime: new Date(formData.endTime).getTime(),
                durationMinutes: Number(formData.durationMinutes),
                isActive: true, 
                allowNegativeMarking: formData.negativeMarking,
                negativeMarkFactor: Number(formData.negativeMarkFactor),
                maxAttempts: Number(formData.maxAttempts),
                contestQuestions: contestQuestions
            };

            await api.post('/contest/create', contestPayload);
            alert('Contest Created Successfully!');
            navigate('/dashboard');
        } catch (err) {
            console.error(err);
            const errorMsg = err.response?.data?.message || err.response?.data || err.message;
            alert('Failed to create contest: ' + errorMsg);
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

                <div className="pt-6 border-t">
                    <h3 className="text-xl font-bold text-slate-800 mb-4">Add Contest Questions</h3>
                    <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-3 mb-6">
                        <div>
                            <label className="block text-slate-700 font-medium mb-1 text-sm">Question Text</label>
                            <textarea className="input-field w-full text-sm" rows="2"
                                value={questionForm.questionText} 
                                onChange={e => setQuestionForm({ ...questionForm, questionText: e.target.value })} 
                                placeholder="Enter question text here..." />
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-slate-700 font-medium mb-1 text-sm">Option A</label>
                                <input type="text" className="input-field w-full text-sm"
                                    value={questionForm.optionA} 
                                    onChange={e => setQuestionForm({ ...questionForm, optionA: e.target.value })} />
                            </div>
                            <div>
                                <label className="block text-slate-700 font-medium mb-1 text-sm">Option B</label>
                                <input type="text" className="input-field w-full text-sm"
                                    value={questionForm.optionB} 
                                    onChange={e => setQuestionForm({ ...questionForm, optionB: e.target.value })} />
                            </div>
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-slate-700 font-medium mb-1 text-sm">Option C</label>
                                <input type="text" className="input-field w-full text-sm"
                                    value={questionForm.optionC} 
                                    onChange={e => setQuestionForm({ ...questionForm, optionC: e.target.value })} />
                            </div>
                            <div>
                                <label className="block text-slate-700 font-medium mb-1 text-sm">Option D</label>
                                <input type="text" className="input-field w-full text-sm"
                                    value={questionForm.optionD} 
                                    onChange={e => setQuestionForm({ ...questionForm, optionD: e.target.value })} />
                            </div>
                        </div>
                        <div className="grid grid-cols-4 gap-4">
                            <div>
                                <label className="block text-slate-700 font-medium mb-1 text-sm">Correct Option</label>
                                <select className="input-field w-full text-sm"
                                    value={questionForm.answer} 
                                    onChange={e => setQuestionForm({ ...questionForm, answer: e.target.value })}>
                                    <option value="A">Option A</option>
                                    <option value="B">Option B</option>
                                    <option value="C">Option C</option>
                                    <option value="D">Option D</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-slate-700 font-medium mb-1 text-sm">Points</label>
                                <input type="number" min="1" className="input-field w-full text-sm"
                                    value={questionForm.points} 
                                    onChange={e => setQuestionForm({ ...questionForm, points: e.target.value })} />
                            </div>
                            <div>
                                <label className="block text-slate-700 font-medium mb-1 text-sm">Difficulty</label>
                                <select className="input-field w-full text-sm"
                                    value={questionForm.difficulty} 
                                    onChange={e => setQuestionForm({ ...questionForm, difficulty: e.target.value })}>
                                    <option value="EASY">Easy</option>
                                    <option value="MEDIUM">Medium</option>
                                    <option value="HARD">Hard</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-slate-700 font-medium mb-1 text-sm">Subject</label>
                                <input type="text" className="input-field w-full text-sm"
                                    value={questionForm.category} 
                                    onChange={e => setQuestionForm({ ...questionForm, category: e.target.value })} />
                            </div>
                        </div>
                        <div className="pt-2 flex justify-end">
                            <button type="button" onClick={handleAddQuestion} className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold px-4 py-2 rounded-lg">
                                Add Question to Contest
                            </button>
                        </div>
                    </div>

                    <div className="space-y-3">
                        <h4 className="font-bold text-slate-700 text-sm">Added Questions ({contestQuestions.length})</h4>
                        {contestQuestions.length === 0 ? (
                            <p className="text-slate-400 text-sm italic">No questions added yet. Please add at least one question.</p>
                        ) : (
                            <div className="border border-slate-200 rounded-xl divide-y divide-slate-200 bg-slate-50/50 max-h-80 overflow-y-auto">
                                {contestQuestions.map((q, idx) => (
                                    <div key={idx} className="p-4 flex justify-between items-start gap-4">
                                        <div className="space-y-1">
                                            <p className="text-sm font-medium text-slate-800"><span className="font-bold mr-1">{idx + 1}.</span> {q.questionText}</p>
                                            <div className="grid grid-cols-2 gap-x-8 gap-y-1 text-xs text-slate-500 font-mono">
                                                <span>A: {q.options[0]}</span>
                                                <span>B: {q.options[1]}</span>
                                                <span>C: {q.options[2]}</span>
                                                <span>D: {q.options[3]}</span>
                                            </div>
                                            <div className="flex gap-4 pt-1 text-xs font-semibold">
                                                <span className="text-emerald-600">Correct: {q.answer}</span>
                                                <span className="text-indigo-600">Points: {q.points}</span>
                                                <span className="text-amber-600">Diff: {q.difficulty}</span>
                                                <span className="text-slate-500">Subject: {q.category}</span>
                                            </div>
                                        </div>
                                        <button type="button" onClick={() => handleRemoveQuestion(idx)} className="text-red-500 hover:text-red-700 text-xs font-semibold">
                                            Remove
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
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
