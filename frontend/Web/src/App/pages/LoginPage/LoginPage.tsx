import {
  Alert,
  Box,
  Button,
  CircularProgress,
  IconButton,
  InputAdornment,
  Paper,
  TextField,
  Typography,
} from '@mui/material';
import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';

import EyeIcon from '@/App/components/icon/EyeIcon';
import HiveLogo from '@/App/components/icon/HiveLogo';
import { useAuth } from '@/App/providers/AuthProvider';

const LoginPage = () => {
  const { signIn } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('admin@beeiot.app');
  const [password, setPassword] = useState('');
  const [showPwd, setShowPwd] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    if (!email || !password) {
      setError('Введите email и пароль');
      return;
    }
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      signIn({ email });
      navigate('/admin/description', { replace: true });
    }, 700);
  };

  return (
    <Box
      sx={{
        height: '100dvh',
        minHeight: '100vh',
        width: '100%',
        boxSizing: 'border-box',
        backgroundColor: 'rgb(244,244,244)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        p: 2,
        backgroundImage: 'radial-gradient(rgba(255,182,39,0.07) 1.5px, transparent 1.5px)',
        backgroundSize: '22px 22px',
      }}
    >
      <Paper
        elevation={0}
        sx={{
          width: '100%',
          maxWidth: 420,
          backgroundColor: '#fff',
          borderRadius: '20px',
          border: '1px solid rgb(255,182,39)',
          boxShadow: '0px 0px 24px rgba(0,0,0,0.08)',
          p: { xs: 3.5, sm: 5 },
        }}
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 3.5 }}>
          <HiveLogo />
          <Typography variant="h4" sx={{ mt: 2, fontWeight: 700, fontSize: 28 }}>
            BeeIoT Admin
          </Typography>
          <Typography variant="body2" sx={{ color: 'rgba(0,0,0,0.55)', mt: 0.5 }}>
            Вход в панель управления
          </Typography>
        </Box>

        <form onSubmit={handleSubmit} noValidate>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <Box>
              <Typography
                variant="body2"
                sx={{ mb: 0.75, fontWeight: 500, color: 'rgba(0,0,0,0.7)' }}
              >
                Email
              </Typography>
              <TextField
                fullWidth
                size="medium"
                placeholder="admin@example.com"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                autoComplete="email"
              />
            </Box>

            <Box>
              <Typography
                variant="body2"
                sx={{ mb: 0.75, fontWeight: 500, color: 'rgba(0,0,0,0.7)' }}
              >
                Пароль
              </Typography>
              <TextField
                fullWidth
                size="medium"
                type={showPwd ? 'text' : 'password'}
                placeholder="••••••••"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                autoComplete="current-password"
                slotProps={{
                  input: {
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          onClick={() => setShowPwd((current) => !current)}
                          edge="end"
                          size="small"
                        >
                          <EyeIcon off={!showPwd} />
                        </IconButton>
                      </InputAdornment>
                    ),
                  },
                }}
              />
            </Box>

            {error ? (
              <Alert severity="error" sx={{ borderRadius: 2 }}>
                {error}
              </Alert>
            ) : null}

            <Button
              type="submit"
              variant="outlined"
              size="large"
              disabled={loading}
              sx={{
                mt: 1,
                fontSize: 16,
                py: 1.4,
                borderRadius: '10px',
                borderWidth: '2px',
                borderColor: 'rgb(255,182,39)',
                bgcolor: '#fff',
                color: '#000',
                fontWeight: 600,
                '&:hover': {
                  borderWidth: '2px',
                  borderColor: 'rgb(228,147,19)',
                  bgcolor: 'rgba(255,182,39,0.12)',
                },
                '&.Mui-disabled': {
                  borderWidth: '2px',
                  borderColor: 'rgba(0,0,0,0.12)',
                  bgcolor: 'rgba(0,0,0,0.03)',
                },
              }}
            >
              {loading ? <CircularProgress size={22} sx={{ color: '#000' }} /> : 'Войти'}
            </Button>
          </Box>
        </form>

        <Typography
          variant="caption"
          sx={{
            display: 'block',
            textAlign: 'center',
            mt: 3,
            color: 'rgba(0,0,0,0.45)',
          }}
        >
          Только для администраторов BeeIoT
        </Typography>
      </Paper>
    </Box>
  );
};

export default LoginPage;
