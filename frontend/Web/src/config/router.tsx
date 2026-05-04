import { type ReactNode } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';

import App from '@/App';
import AdminPage from '@/App/pages/AdminPage';
import DescriptionPage from '@/App/pages/DescriptionPage';
import InstructionPage from '@/App/pages/InstructionPage';
import LoginPage from '@/App/pages/LoginPage';
import NotFoundPage from '@/App/pages/NotFoundPage';
import { useAuth } from '@/App/providers/AuthProvider';
import FullScreenLoader from '@/components/FullScreenLoader/FullScreenLoader';
import RouterErrorBoundary from '@/components/RouterErrorBoundary/RouterErrorBoundary';

const RequireAuth = ({ children }: { children: ReactNode }) => {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/auth" replace />;
  }
  return children;
};

const HomeRedirect = () => {
  const { user } = useAuth();
  return <Navigate to={user ? '/admin/description' : '/auth'} replace />;
};

const AuthRedirect = () => {
  const { user } = useAuth();
  return user ? <Navigate to="/admin/description" replace /> : <LoginPage />;
};

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    hydrateFallbackElement: <FullScreenLoader />,
    children: [
      { index: true, element: <HomeRedirect /> },
      { path: 'auth', element: <AuthRedirect /> },
      {
        path: 'admin',
        element: (
          <RequireAuth>
            <AdminPage />
          </RequireAuth>
        ),
        errorElement: <RouterErrorBoundary />,
        children: [
          { index: true, element: <Navigate to="description" replace /> },
          { path: 'description', element: <DescriptionPage /> },
          { path: 'instruction', element: <InstructionPage /> },
        ],
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
