package handlers

import (
	"BeeIOT/internal/domain/models/httpType"
	"net/http"
	"time"
)

func (h *Handler) SetHiveWeight(w http.ResponseWriter, r *http.Request) {
	email, err := h.getEmailFromContext(w, r)
	if err != nil {
		return
	}

	var weight httpType.HiveWeight // не нравится мне
	err = h.readBodyJSON(w, r, &weight)
	if err != nil {
		return
	}
	weight.Email = email

	err = h.db.NewHiveWeight(r.Context(), weight)
	if err != nil {
		h.logger.Error().Err(err).Str("email", email).Msg("failed to add weight")
		http.Error(w, "Внутренняя ошибка сервера", http.StatusInternalServerError)
		return
	}

	h.writeBodyJSON(w, "Данные веса успешно установлены", nil)
}

func (h *Handler) DeleteHiveWeight(w http.ResponseWriter, r *http.Request) {
	email, err := h.getEmailFromContext(w, r)
	if err != nil {
		return
	}

	var weight httpType.HiveWeight // не нравится мне
	if err = h.readBodyJSON(w, r, &weight); err != nil {
		return
	}
	weight.Email = email

	err = h.db.DeleteHiveWeight(r.Context(), weight)
	if err != nil {
		h.logger.Error().Err(err).Str("email", email).Msg("failed to delete weight")
		http.Error(w, "Внутренняя ошибка сервера", http.StatusInternalServerError)
		return
	}

	h.writeBodyJSON(w, "Вес удален успешно", nil)
}

func (h *Handler) GetNoiseSinceTime(w http.ResponseWriter, r *http.Request) {
	email, err := h.getEmailFromContext(w, r)
	if err != nil {
		return
	}

	hiveName := r.URL.Query().Get("name")
	if hiveName == "" {
		h.logger.Warn().Str("email", email).Msg("missing query param 'name'")
		http.Error(w, "Параметр \"name\" обязателен", http.StatusBadRequest)
		return
	}

	since, ok := parsePeriod(r.URL.Query().Get("period"))
	if !ok {
		h.logger.Warn().Str("email", email).Str("period", r.URL.Query().Get("period")).Msg("invalid period")
		http.Error(w, "Неверный параметр period (допустимо: day, week, month)", http.StatusBadRequest)
		return
	}

	noiseLevels, err := h.db.GetNoiseSinceTime(r.Context(), email, hiveName, since)
	if err != nil {
		h.logger.Error().Err(err).Str("email", email).Str("hive", hiveName).Msg("failed to get noise data")
		http.Error(w, "Внутренняя ошибка сервера", http.StatusInternalServerError)
		return
	}

	h.writeBodyJSON(w, "Данные шума успешно получены", noiseLevels)
}

func parsePeriod(period string) (time.Time, bool) {
	now := time.Now()
	switch period {
	case "", "day":
		return now.AddDate(0, 0, -1), true
	case "week":
		return now.AddDate(0, 0, -7), true
	case "month":
		return now.AddDate(0, -1, 0), true
	default:
		return time.Time{}, false
	}
}
