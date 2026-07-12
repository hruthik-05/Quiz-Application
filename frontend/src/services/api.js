import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8200/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
    xsrfCookieName: 'XSRF-TOKEN',
    xsrfHeaderName: 'X-XSRF-TOKEN'
});

export default api;
