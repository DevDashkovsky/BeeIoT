import { createBrowserRouter, Navigate, redirect } from 'react-router-dom';

import App from '@/App';
import AuthPage from '@/App/pages/AuthPage';
import FullScreenLoader from '@/components/FullScreenLoader/FullScreenLoader';
import RouterErrorBoundary from '@/components/RouterErrorBoundary/RouterErrorBoundary';
import { useAuthStore } from '@/store/useAuthStore';

import { queryClient } from './queryClient';

import NotFoundPage from '@/App/pages/NotFoundPage';
import { sessionQueryOptions } from '@/hooks/queries/useSessionQuery';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    hydrateFallbackElement: <FullScreenLoader />,
    children: [
      { index: true, element: <Navigate to="/auth" replace /> },
      { path: 'auth', element: <AuthPage /> },
      {
        id: 'protected',
        loader: async () => {
          const token = useAuthStore.getState().token;
          if (!token) {
            throw redirect('/auth');
          }
          try {
            await queryClient.ensureQueryData(sessionQueryOptions());
          } catch {
            throw redirect('/auth');
          }
          return null;
        },
        errorElement: <RouterErrorBoundary />,
        children: [
          {
            path: 'admin',
            lazy: async () => {
              const { default: AdminPage } = await import('@/App/pages/AdminPage');
              return { Component: AdminPage };
            },
          },
        ],
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
