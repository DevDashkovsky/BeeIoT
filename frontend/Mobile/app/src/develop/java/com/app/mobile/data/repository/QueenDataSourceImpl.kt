package com.app.mobile.data.repository

import android.content.Context
import com.app.mobile.data.api.models.ApiResult
import com.app.mobile.data.mock.MockDataProvider
import com.app.mobile.domain.models.DateRange
import com.app.mobile.domain.models.hives.queen.AdultStage
import com.app.mobile.domain.models.hives.queen.EggStage
import com.app.mobile.domain.models.hives.queen.LarvaStage
import com.app.mobile.domain.models.hives.queen.PupaStage
import com.app.mobile.domain.models.hives.queen.QueenDomain
import com.app.mobile.domain.models.hives.queen.QueenDomainPreview
import com.app.mobile.domain.models.hives.queen.QueenLifecycle
import com.app.mobile.domain.repository.datasource.QueenDataSource
import kotlinx.coroutines.delay
import java.time.LocalDate

class QueenDataSourceImpl(private val context: Context) : QueenDataSource {

    override suspend fun getQueens(): ApiResult<List<QueenDomainPreview>> {
        delay(100)
        return ApiResult.Success(
            listOf(
                QueenDomainPreview(name = "Матка-1", startDate = LocalDate.of(2026, 2, 19)),
                QueenDomainPreview(name = "Матка-2", startDate = LocalDate.of(2026, 3, 1))
            )
        )
    }

    override suspend fun getQueen(name: String): ApiResult<QueenDomain> {
        delay(100)
        val lifecycle = MockDataProvider.getQueenLifecycle(context)
        return ApiResult.Success(QueenDomain(name = name, stages = lifecycle))
    }

    override suspend fun createQueen(name: String, startDate: LocalDate): ApiResult<QueenDomain> {
        delay(100)
        val lifecycle = MockDataProvider.getQueenLifecycle(context)
        return ApiResult.Success(QueenDomain(name = name, stages = lifecycle))
    }

    override suspend fun updateQueen(oldName: String, newName: String?, startDate: String?): ApiResult<Unit> {
        delay(100)
        return ApiResult.Success(Unit)
    }

    override suspend fun deleteQueen(name: String): ApiResult<Unit> {
        delay(100)
        return ApiResult.Success(Unit)
    }

    override suspend fun getQueensWithCalendars(): ApiResult<List<QueenDomain>> {
        delay(100)
        val mockLifecycle = MockDataProvider.getQueenLifecycle(context)
        val testLifecycle = buildTestLifecycleWithSelectionToday(LocalDate.now())
        return ApiResult.Success(
            listOf(
                QueenDomain(name = "Матка-1", stages = testLifecycle),
                QueenDomain(name = "Матка-2", stages = mockLifecycle)
            )
        )
    }

    // Матка-1 с pupa.selectionDate = today для ручного тестирования критического уведомления
    private fun buildTestLifecycleWithSelectionToday(today: LocalDate): QueenLifecycle {
        val birthDate = today.minusDays(14)
        return QueenLifecycle(
            birthDate = birthDate,
            egg = EggStage(
                day0Standing = birthDate,
                day1Tilted = birthDate.plusDays(1),
                day2Lying = birthDate.plusDays(2)
            ),
            larva = LarvaStage(
                hatchDate = birthDate.plusDays(3),
                feedingDays = (0..4).map { birthDate.plusDays(3L + it) },
                sealedDate = birthDate.plusDays(8)
            ),
            pupa = PupaStage(
                period = DateRange(
                    start = birthDate.plusDays(9),
                    end = birthDate.plusDays(16)
                ),
                selectionDate = today
            ),
            adult = AdultStage(
                emergence = DateRange(today.plusDays(2), today.plusDays(3)),
                maturation = DateRange(today.plusDays(3), today.plusDays(6)),
                matingFlight = DateRange(today.plusDays(6), today.plusDays(11)),
                insemination = DateRange(today.plusDays(7), today.plusDays(8)),
                checkLaying = DateRange(today.plusDays(14), today.plusDays(18))
            )
        )
    }
}
