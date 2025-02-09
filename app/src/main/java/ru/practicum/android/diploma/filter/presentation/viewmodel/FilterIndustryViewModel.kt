package ru.practicum.android.diploma.filter.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.sharedprefs.interactor.SharedPrefsInteractor
import ru.practicum.android.diploma.common.sharedprefs.models.IndustrySP
import ru.practicum.android.diploma.common.util.debounce
import ru.practicum.android.diploma.filter.domain.interactor.FilterInteractor
import ru.practicum.android.diploma.filter.domain.model.Industry
import ru.practicum.android.diploma.filter.domain.model.IndustryViewState

class FilterIndustryViewModel(
    private val filterInteractor: FilterInteractor,
    private val sharedPrefsInteractor: SharedPrefsInteractor
) : ViewModel() {
    private var selectedIndustry: Industry? = sharedPrefsInteractor.getFilter().industrySP?.let { industryFromSP ->
        convertIndustrySP(industryFromSP) }
    private val stateLiveData = MutableLiveData<IndustryViewState>()
    fun observeState(): LiveData<IndustryViewState> = stateLiveData

    private var lastSearchText: String? = null

    private val industriesSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { changedText ->
        getIndustries(changedText)
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText != changedText) {
            lastSearchText = changedText
            industriesSearchDebounce(changedText)
        }
    }
    private fun getIndustries(query: String) { // получить список всех индустрий
        val lowerCaseQuery = query.lowercase()
        renderState(IndustryViewState.Loading)
        viewModelScope.launch {
            filterInteractor
                .getIndustries()
                .collect { viewState ->
                    when (viewState) {
                        is IndustryViewState.Success -> {
                            // Фильтруем список индустрий
                            val filteredIndustries = viewState.industryList.filter { industry ->
                                industry.name.lowercase().contains(lowerCaseQuery)
                            }
                            if (filteredIndustries.isNotEmpty()) {
                                // Обновляем состояние с отфильтрованным списком
                                renderState(IndustryViewState.Success(filteredIndustries))
                            } else {
                                renderState(IndustryViewState.NotFoundError)
                            }
                        }
                        else -> {
                            // Если состояние не Success, просто рендерим его
                            renderState(viewState)
                        }
                    }
                }
        }
    }

    private fun renderState(state: IndustryViewState) {
        stateLiveData.postValue(state)
    }

    fun setIndustry(industry: Industry) {
        selectedIndustry = industry.copy(selected = false)
    }

    fun getIndustrySP(): IndustrySP? =
        selectedIndustry?.let {
            IndustrySP(
                id = it.id,
                name = it.name
            )
        }

    fun getSelectedIndustry(): Industry? {
        return selectedIndustry
    }

    fun convertIndustrySP(industrySP: IndustrySP?): Industry? =
        industrySP?.let {
            Industry(
                id = it.id,
                name = it.name
            )
        }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 300L
    }
}
