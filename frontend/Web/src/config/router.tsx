import { createBrowserRouter } from 'react-router-dom';

import AdminPage from '@/App/pages/AdminPage/AdminPage';
import AuthPage from '@/App/pages/AuthPage/AuthPage';

export const router = createBrowserRouter([
  { path: '/', element: <AuthPage /> },
  { path: '/admin', element: <AdminPage /> },
]);
