import { CssBaseline, ThemeProvider } from '@mui/material';
import { Outlet } from 'react-router-dom';

import { AuthProvider } from './providers/AuthProvider';
import beeTheme from './theme';

const App = () => {
  return (
    <ThemeProvider theme={beeTheme}>
      <CssBaseline />
      <AuthProvider>
        <Outlet />
      </AuthProvider>
    </ThemeProvider>
  );
};

export default App;
