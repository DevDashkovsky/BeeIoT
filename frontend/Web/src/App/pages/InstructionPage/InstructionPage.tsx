import {
  Alert as InsAlert,
  Box as InsBox,
  Button as InsButton,
  Paper as InsPaper,
  Snackbar as InsSnackbar,
  Typography as InsTypography,
} from '@mui/material';
import { useRef as insUseRef, useState as insUseState, type DragEvent } from 'react';

import IconDrag from '@/App/components/icon/IconDrag';
import IconPlus from '@/App/components/icon/IconPlus';

import PageHeader, { type PageHeaderStatus } from '../../components/PageHeader';

import InstructionItem, { type DragPosition, type InstructionItemData } from './InstructionItem';

type DragOverInfo = {
  id: string | null;
  pos: DragPosition;
};

type SnackbarState = null | {
  severity: 'success' | 'error' | 'info' | 'warning';
  text: string;
};

const DEFAULT_ITEMS: InstructionItemData[] = [
  {
    id: 'i1',
    title: 'Подключение хаба',
    body: 'Включите хаб и убедитесь, что светодиод горит зелёным. В приложении нажмите «Добавить хаб» и введите серийный номер с задней панели устройства.',
    open: true,
  },
  {
    id: 'i2',
    title: 'Создание улья',
    body: 'На главном экране нажмите «+» рядом с «Мои ульи». Введите название улья и привяжите датчик из выпадающего списка доступных.',
    open: false,
  },
  {
    id: 'i3',
    title: 'Просмотр телеметрии',
    body: 'Откройте улей и переключайте вкладки «Шум», «Температура», «Вес». Тяните график в стороны, чтобы видеть историю.',
    open: false,
  },
];

const uid = () => `i${Math.random().toString(36).slice(2, 9)}`;

const InstructionPage = () => {
  const [items, setItems] = insUseState<InstructionItemData[]>(DEFAULT_ITEMS);
  const [initial, setInitial] = insUseState<InstructionItemData[]>(DEFAULT_ITEMS);
  const [saving, setSaving] = insUseState(false);
  const [snack, setSnack] = insUseState<SnackbarState>(null);

  const [draggingId, setDraggingId] = insUseState<string | null>(null);
  const [overInfo, setOverInfo] = insUseState<DragOverInfo>({ id: null, pos: null });
  const listEndRef = insUseRef<HTMLDivElement | null>(null);

  const stripOpen = (arr: InstructionItemData[]) =>
    arr.map(({ id, title, body }) => ({ id, title, body }));

  const dirty = JSON.stringify(stripOpen(items)) !== JSON.stringify(stripOpen(initial));

  const updateItem = (id: string, patch: Partial<InstructionItemData>) =>
    setItems((arr) => arr.map((it) => (it.id === id ? { ...it, ...patch } : it)));

  const toggleItem = (id: string) =>
    setItems((arr) => arr.map((it) => (it.id === id ? { ...it, open: !it.open } : it)));

  const deleteItem = (id: string) => {
    if (!confirm('Удалить этот пункт?')) return;
    setItems((arr) => arr.filter((it) => it.id !== id));
  };

  const moveItem = (id: string, dir: number) => {
    setItems((arr) => {
      const idx = arr.findIndex((it) => it.id === id);
      if (idx < 0) return arr;
      const nextIndex = idx + dir;
      if (nextIndex < 0 || nextIndex >= arr.length) return arr;
      const next = [...arr];
      [next[idx], next[nextIndex]] = [next[nextIndex], next[idx]];
      return next;
    });
  };

  const addItem = () => {
    const next = { id: uid(), title: '', body: '', open: true };
    setItems((arr) => [...arr, next]);
    setTimeout(() => listEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' }), 50);
  };

  const expandAll = () => setItems((arr) => arr.map((it) => ({ ...it, open: true })));
  const collapseAll = () => setItems((arr) => arr.map((it) => ({ ...it, open: false })));

  const handleDragStart = (id: string) => (event: DragEvent<HTMLDivElement>) => {
    setDraggingId(id);
    try {
      event.dataTransfer.effectAllowed = 'move';
      event.dataTransfer.setData('text/plain', id);
    } catch {
      // noop
    }
  };

  const handleDragEnd = () => {
    setDraggingId(null);
    setOverInfo({ id: null, pos: null });
  };

  const handleDragOver = (id: string) => (event: DragEvent<HTMLDivElement>) => {
    if (!draggingId || draggingId === id) return;
    event.preventDefault();
    try {
      event.dataTransfer.dropEffect = 'move';
    } catch {
      // noop
    }
    const rect = event.currentTarget.getBoundingClientRect();
    const offsetY = event.clientY - rect.top;
    const pos: DragPosition = offsetY < rect.height / 2 ? 'before' : 'after';
    setOverInfo((prev) => (prev.id === id && prev.pos === pos ? prev : { id, pos }));
  };

  const handleDragLeave = (id: string) => (event: DragEvent<HTMLDivElement>) => {
    const rect = event.currentTarget.getBoundingClientRect();
    if (
      event.clientX < rect.left ||
      event.clientX > rect.right ||
      event.clientY < rect.top ||
      event.clientY > rect.bottom
    ) {
      setOverInfo((prev) => (prev.id === id ? { id: null, pos: null } : prev));
    }
  };

  const handleDrop = (targetId: string) => (event: DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    if (!draggingId || draggingId === targetId) {
      handleDragEnd();
      return;
    }
    const pos = overInfo.pos || 'before';
    setItems((arr) => {
      const fromIdx = arr.findIndex((it) => it.id === draggingId);
      if (fromIdx < 0) return arr;
      const dragged = arr[fromIdx];
      const without = arr.filter((it) => it.id !== draggingId);
      let toIdx = without.findIndex((it) => it.id === targetId);
      if (toIdx < 0) return arr;
      if (pos === 'after') toIdx += 1;
      const next = [...without];
      next.splice(toIdx, 0, dragged);
      return next;
    });
    handleDragEnd();
  };

  const handleSave = () => {
    setSaving(true);
    setTimeout(() => {
      setInitial(items);
      setSaving(false);
      setSnack({
        severity: 'success',
        text: `Сохранено ${items.length} ${pluralize(items.length, ['пункт', 'пункта', 'пунктов'])}`,
      });
    }, 600);
  };

  const status: PageHeaderStatus = {
    label: `${items.length} ${pluralize(items.length, ['пункт', 'пункта', 'пунктов'])}`,
    bg: 'rgba(255,182,39,0.18)',
    fg: 'rgb(120,80,0)',
  };

  return (
    <InsBox>
      <PageHeader
        title="Инструкция"
        subtitle="Аккордеон на экране «Как пользоваться приложением»"
        status={status}
        onSave={handleSave}
        saving={saving}
        dirty={dirty}
      />

      <InsPaper
        elevation={0}
        sx={{
          backgroundColor: '#fff',
          border: '1.5px solid rgba(0,0,0,0.08)',
          borderRadius: '16px',
          p: { xs: 2.5, sm: 3.5 },
          boxShadow: 'none',
        }}
      >
        <InsBox
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            gap: 2,
            mb: 2.5,
            flexWrap: 'wrap',
          }}
        >
          <InsTypography sx={{ fontSize: 13, color: 'rgba(0,0,0,0.55)' }}>
            Перетаскивайте за{' '}
            <InsBox
              component="span"
              sx={{
                display: 'inline-flex',
                verticalAlign: 'middle',
                mx: 0.5,
                color: 'rgba(0,0,0,0.55)',
              }}
            >
              <IconDrag />
            </InsBox>{' '}
            или используйте стрелки
          </InsTypography>
          <InsBox sx={{ display: 'flex', gap: 1 }}>
            <InsButton variant="outlined" size="small" onClick={expandAll} sx={ghostBtnSx}>
              Раскрыть все
            </InsButton>
            <InsButton variant="outlined" size="small" onClick={collapseAll} sx={ghostBtnSx}>
              Свернуть все
            </InsButton>
          </InsBox>
        </InsBox>

        <InsBox sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
          {items.map((item, idx) => (
            <InstructionItem
              key={item.id}
              item={item}
              index={idx}
              total={items.length}
              onUpdate={updateItem}
              onDelete={deleteItem}
              onMove={moveItem}
              onToggle={toggleItem}
              isDragging={draggingId === item.id}
              isOver={overInfo.id === item.id}
              dragPos={overInfo.id === item.id ? overInfo.pos : null}
              onDragStart={handleDragStart(item.id)}
              onDragEnd={handleDragEnd}
              onDragOver={handleDragOver(item.id)}
              onDragLeave={handleDragLeave(item.id)}
              onDrop={handleDrop(item.id)}
            />
          ))}
          {items.length === 0 ? (
            <InsBox
              sx={{
                py: 6,
                textAlign: 'center',
                border: '2px dashed rgba(0,0,0,0.12)',
                borderRadius: '14px',
                color: 'rgba(0,0,0,0.5)',
              }}
            >
              <InsTypography sx={{ fontSize: 14, mb: 1 }}>Нет ни одного пункта</InsTypography>
              <InsTypography sx={{ fontSize: 12 }}>
                Добавьте первый пункт инструкции, чтобы начать
              </InsTypography>
            </InsBox>
          ) : null}
          <div ref={listEndRef} />
        </InsBox>

        <InsBox sx={{ mt: 2.5 }}>
          <InsButton
            fullWidth
            variant="outlined"
            onClick={addItem}
            startIcon={<IconPlus />}
            sx={{
              borderRadius: '12px',
              borderStyle: 'dashed',
              borderWidth: '1.5px',
              borderColor: 'rgba(255,182,39,0.7)',
              color: 'rgb(120,80,0)',
              backgroundColor: 'rgba(255,182,39,0.04)',
              py: 1.5,
              fontWeight: 600,
              '&:hover': {
                borderColor: 'rgb(255,182,39)',
                backgroundColor: 'rgba(255,182,39,0.12)',
                borderStyle: 'dashed',
                borderWidth: '1.5px',
              },
            }}
          >
            Добавить пункт
          </InsButton>
        </InsBox>
      </InsPaper>

      <InsSnackbar
        open={Boolean(snack)}
        autoHideDuration={3000}
        onClose={() => setSnack(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        {snack ? (
          <InsAlert severity={snack.severity} variant="filled" sx={{ borderRadius: 2 }}>
            {snack.text}
          </InsAlert>
        ) : undefined}
      </InsSnackbar>
    </InsBox>
  );
};

const ghostBtnSx = {
  borderRadius: '10px',
  borderColor: 'rgba(0,0,0,0.15)',
  color: 'rgba(0,0,0,0.7)',
  fontSize: 12,
  fontWeight: 600,
  textTransform: 'none',
  px: 1.5,
  '&:hover': {
    borderColor: 'rgb(255,182,39)',
    bgcolor: 'rgba(255,182,39,0.08)',
    color: '#000',
  },
};

const pluralize = (value: number, forms: [string, string, string]) => {
  const a = Math.abs(value) % 100;
  const b = a % 10;
  if (a > 10 && a < 20) return forms[2];
  if (b > 1 && b < 5) return forms[1];
  if (b === 1) return forms[0];
  return forms[2];
};

export default InstructionPage;
