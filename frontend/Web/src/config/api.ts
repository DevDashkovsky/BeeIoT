import axios from 'axios';

import { getRevalidator } from '@/config/revalidator';
import { useAuthStore } from '@/store/useAuthStore';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8000/api',
  timeout: 5000,
});

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().logout();
      getRevalidator()?.revalidate();
    }
    return Promise.reject(error);
  }
);
