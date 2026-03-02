package handlers

import (
	"BeeIOT/internal/domain/models/httpType"
	"net/http"
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
