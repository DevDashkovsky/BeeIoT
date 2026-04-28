import { Box, Stack, Typography } from '@mui/material';
import { useState } from 'react';

import { PasswordTextField, PrimaryButton, ValidatedTextField } from '@/components/ui';
import { colors } from '@/config/theme';

const AuthPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: colors.bgSecondary,
        px: { xs: 2, sm: 4 },
        py: { xs: 4, sm: 6 },
      }}
    >
      <Box
        sx={{
          width: '100%',
          maxWidth: 440,
          backgroundColor: 'background.paper',
          borderRadius: 3,
          border: '1px solid',
          borderColor: 'divider',
          boxShadow: '0 20px 50px rgba(20, 20, 20, 0.08)',
          px: { xs: 5, sm: 7, md: 9 },
          py: { xs: 6, sm: 8 },
          display: 'flex',
          flexDirection: 'column',
          gap: 4,
        }}
      >
        <Box>
          <Typography variant="h4">Авторизация</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Вход только для администратора
          </Typography>
        </Box>

        <Stack spacing={1.5}>
          <ValidatedTextField
            value={email}
            onChange={setEmail}
            placeholder="Email"
            supportingText=" "
          />
          <PasswordTextField
            value={password}
            onChange={setPassword}
            placeholder="Пароль"
            supportingText=" "
          />
        </Stack>

        <Box sx={{ display: 'flex', justifyContent: 'center' }}>
          <PrimaryButton
            onClick={() => undefined}
            fullWidth={false}
            sx={{ px: 6, py: 1.5, minWidth: 200 }}
          >
            Войти
          </PrimaryButton>
        </Box>
      </Box>
    </Box>
  );
};

export default AuthPage;
