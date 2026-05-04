import { Alert as DescAlert, Box as DescBox, Paper as DescPaper, Snackbar } from '@mui/material';
import { useState as descUseState } from 'react';

import FieldBlock from './FieldBlock';
import PageHeader, { type PageHeaderStatus } from '../../components/PageHeader';

type DescriptionData = {
  title: string;
  short: string;
  full: string;
};

type SnackbarState = null | {
  severity: 'success' | 'error' | 'info' | 'warning';
  text: string;
};

const DEFAULT_DESCRIPTION: DescriptionData = {
  title: 'BeeIoT — мониторинг ульев',
  short: 'Контролируйте здоровье пасеки в реальном времени.',
  full: `BeeIoT — это система IoT-мониторинга пчелиных ульев. Подключите хаб с датчиками и получайте телеметрию по температуре, шуму и весу улья прямо в телефон.

Ведите учёт маток, фиксируйте выполненные работы, получайте уведомления о критических событиях. Всё, что нужно пасечнику, — в одном приложении.`,
};

const DescriptionPage = () => {
  const [data, setData] = descUseState<DescriptionData>(DEFAULT_DESCRIPTION);
  const [initial, setInitial] = descUseState<DescriptionData>(DEFAULT_DESCRIPTION);
  const [saving, setSaving] = descUseState(false);
  const [snack, setSnack] = descUseState<SnackbarState>(null);

  const dirty = JSON.stringify(data) !== JSON.stringify(initial);

  const update = (key: keyof DescriptionData, value: string) =>
    setData((prev) => ({ ...prev, [key]: value }));

  const handleSave = () => {
    setSaving(true);
    setTimeout(() => {
      setInitial(data);
      setSaving(false);
      setSnack({ severity: 'success', text: 'Описание сохранено' });
    }, 600);
  };

  const status: PageHeaderStatus = {
    label: 'Опубликовано',
    bg: 'rgba(34,139,34,0.12)',
    fg: 'rgb(28,108,28)',
  };

  return (
    <DescBox>
      <PageHeader
        title="Описание приложения"
        subtitle="Тексты, которые видят пользователи на странице «О приложении»"
        status={status}
        onSave={handleSave}
        saving={saving}
        dirty={dirty}
      />

      <DescPaper
        elevation={0}
        sx={{
          backgroundColor: '#fff',
          border: '1px solid rgba(0,0,0,0.06)',
          borderRadius: '16px',
          p: { xs: 2.5, sm: 4 },
          boxShadow: '0px 1px 2px rgba(0,0,0,0.03)',
        }}
      >
        <DescBox sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          <FieldBlock
            label="Заголовок"
            hint="Отображается крупным шрифтом в шапке экрана «О приложении»"
            value={data.title}
            onChange={(value) => update('title', value)}
            counter={[data.title.length, 80]}
            maxLength={80}
          />
          <FieldBlock
            label="Краткое описание"
            hint="Одна строка под заголовком — суть приложения"
            value={data.short}
            onChange={(value) => update('short', value)}
            counter={[data.short.length, 160]}
            maxLength={160}
          />
          <FieldBlock
            label="Полное описание"
            hint="Многострочный текст. Поддерживаются абзацы (Enter)"
            value={data.full}
            onChange={(value) => update('full', value)}
            multiline
            rows={8}
            counter={[data.full.length, 2000]}
            maxLength={2000}
          />
        </DescBox>
      </DescPaper>

      <Snackbar
        open={Boolean(snack)}
        autoHideDuration={3000}
        onClose={() => setSnack(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        {snack ? (
          <DescAlert severity={snack.severity} variant="filled" sx={{ borderRadius: 2 }}>
            {snack.text}
          </DescAlert>
        ) : undefined}
      </Snackbar>
    </DescBox>
  );
};

export default DescriptionPage;
