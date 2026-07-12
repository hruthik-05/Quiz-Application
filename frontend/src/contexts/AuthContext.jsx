/* eslint-disable react-refresh/only-export-components */
import { createContext, useState, useContext, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkSession = async () => {
            try {
                const response = await api.get('/auth/me');
                setUser(response.data);
                localStorage.setItem('user', JSON.stringify(response.data));
            } catch (error) {
                console.log("No valid session found.");
                setUser(null);
                localStorage.removeItem('user');
            } finally {
                setLoading(false);
            }
        };
        checkSession();
    }, []);

    const login = async (username, password) => {
        try {
            const response = await api.post('/auth/signin', { username, password });
            const userData = response.data;

            localStorage.setItem('user', JSON.stringify(userData));
            setUser(userData);
            return { success: true };
        } catch (error) {
            console.error("Login Error:", error);
            return {
                success: false,
                message: error.response?.data?.message || 'Login failed'
            };
        }
    };

    const register = async (username, email, password, roles = ['user']) => {
        try {
            await api.post('/auth/signup', { username, email, password, roles });
            return { success: true };
        } catch (error) {
            return {
                success: false,
                message: error.response?.data?.message || 'Registration failed'
            };
        }
    };

    const logout = async () => {
        try {
            await api.post('/auth/signout');
        } catch (error) {
            console.error("Logout Error:", error);
        } finally {
            localStorage.removeItem('user');
            setUser(null);
        }
    };

    const updateUser = (userData) => {
        const currentUser = JSON.parse(localStorage.getItem('user'));
        const updated = { ...currentUser, ...userData };
        localStorage.setItem('user', JSON.stringify(updated));
        setUser(updated);
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, updateUser, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
