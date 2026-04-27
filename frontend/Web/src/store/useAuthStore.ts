import { create } from 'zustand';
import { persist } from 'zustand/middleware';

import type { UserInfo } from '@/types/userType';

type AuthState = {
  token: string | null;
  user: UserInfo | null;
  setToken: (token: string | null) => void;
  setUser: (user: UserInfo | null) => void;
  logout: () => void;
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      setToken: (token) => set({ token }),
      setUser: (user) => set({ user }),
      logout: () => set({ token: null, user: null }),
    }),
    {
      name: 'auth-storage',
    }
  )
);
