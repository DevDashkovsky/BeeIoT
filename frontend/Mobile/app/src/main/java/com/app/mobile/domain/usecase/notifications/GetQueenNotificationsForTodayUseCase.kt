package com.app.mobile.domain.usecase.notifications

import com.app.mobile.data.api.models.ApiResult
import com.app.mobile.domain.models.hives.queen.QueenLifecycle
import com.app.mobile.domain.models.notifications.QueenNotificationEvent
import com.app.mobile.domain.repository.datasource.QueenDataSource
import com.app.mobile.domain.repository.notifications.SentNotificationKeyRepository
import java.time.LocalDate

class GetQueenNotificationsForTodayUseCase(
    private val queenDataSource: QueenDataSource,
    private val sentKeyRepository: SentNotificationKeyRepository
) {

    suspend operator fun invoke(today: LocalDate = LocalDate.now()): List<QueenNotificationEvent> {
        val result = queenDataSource.getQueensWithCalendars()
        if (result !is ApiResult.Success) return emptyList()

        return result.data.flatMap { queen ->
            buildEvents(queen.name, queen.stages, today)
                .filter { !sentKeyRepository.isAlreadySent(it.eventKey) }
        }
    }

    private fun buildEvents(
        name: String,
        lifecycle: QueenLifecycle,
        today: LocalDate
    ): List<QueenNotificationEvent> = buildList {
        with(lifecycle) {
            if (today == egg.day0Standing) add(regular(name, "egg_start", today,
                title = "Матка $name",
                body = "Яйцо отложено. Начало цикла развития."))

            if (today == larva.hatchDate) add(regular(name, "larva_start", today,
                title = "Матка $name",
                body = "Личинка вылупилась. Начало стадии кормления."))

            if (today == larva.sealedDate) add(regular(name, "larva_sealed", today,
                title = "Матка $name",
                body = "Ячейка запечатана. Матка переходит в стадию куколки."))

            if (today == pupa.period.start) add(regular(name, "pupa_start", today,
                title = "Матка $name",
                body = "Начало стадии куколки."))

            if (today == pupa.selectionDate) add(critical(name, "pupa_selection", today,
                title = "Матка $name — нужно действие!",
                body = "Сегодня рекомендуемый день отбора маточников. Не пропустите!"))

            if (today == adult.emergence.start) add(regular(name, "emergence_start", today,
                title = "Матка $name",
                body = "Начало периода выхода матки из маточника."))

            if (today == adult.matingFlight.start) add(regular(name, "mating_flight", today,
                title = "Матка $name",
                body = "Начало брачного облёта."))

            if (today == adult.insemination.start) add(regular(name, "insemination_start", today,
                title = "Матка $name",
                body = "Период осеменения. При искусственном осеменении — время действовать."))

            if (today == adult.checkLaying.start) add(critical(name, "check_laying", today,
                title = "Матка $name — нужно действие!",
                body = "Начало периода проверки яйцекладки. Осмотрите улей!"))
        }
    }

    private fun regular(queenName: String, stageKey: String, today: LocalDate, title: String, body: String) =
        QueenNotificationEvent(
            queenName = queenName,
            eventKey = "$queenName|$stageKey|$today",
            title = title,
            body = body,
            isCritical = false
        )

    private fun critical(queenName: String, stageKey: String, today: LocalDate, title: String, body: String) =
        QueenNotificationEvent(
            queenName = queenName,
            eventKey = "$queenName|$stageKey|$today",
            title = title,
            body = body,
            isCritical = true
        )
}
