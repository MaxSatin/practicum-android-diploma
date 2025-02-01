package ru.practicum.android.diploma.filter.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filter.domain.model.IndustryViewState
import ru.practicum.android.diploma.filter.domain.model.RegionViewState

interface FilterRepository {
    fun getIndustries(): Flow<IndustryViewState>
    fun searchRegionsById(parentId: Int): Flow<RegionViewState>
    fun getAllRegions(): Flow<RegionViewState>
//    fun getAllNonCisRegions(): Flow<RegionViewState>
}
