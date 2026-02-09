/* eslint-disable react-refresh/only-export-components */
import { createContext, useState, useContext } from 'react';
import api from '../services/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        const storedUser = localStorage.getItem('user');
        const token = localStorage.getItem('token');
        if (storedUser && token) {
            return JSON.parse(storedUser);
        }
        return null;
    });


    const loading = false;

    const login = async (username, password) => {
        try {
            const response = await api.post('/auth/signin', { username, password });
            const { token, ...userData } = response.data;

            localStorage.setItem('token', token);
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

    const loginWithToken = async (token) => {
        try {
            localStorage.setItem('token', token);

            const response = await api.get('/auth/me');
            const userData = response.data;

            const userToStore = { ...userData, token }; 

            localStorage.setItem('user', JSON.stringify(userToStore));
            setUser(userToStore);
            return { success: true };
        } catch (error) {
            console.error("Token Login Error:", error);
            localStorage.removeItem('token');
            return { success: false, message: 'Failed to fetch user details' };
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
    };

    const updateUser = (userData) => {
        const currentUser = JSON.parse(localStorage.getItem('user'));
        const updated = { ...currentUser, ...userData };
        localStorage.setItem('user', JSON.stringify(updated));
        setUser(updated);
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loginWithToken, updateUser, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
