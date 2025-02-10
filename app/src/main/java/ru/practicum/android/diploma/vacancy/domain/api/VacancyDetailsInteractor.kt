package ru.practicum.android.diploma.vacancy.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.vacancy.presentation.VacancyScreenState

interface VacancyDetailsInteractor {
    fun getVacancyDetails(vacancyId: String, isFromFavoritesScreen: Boolean): Flow<VacancyScreenState>
}
