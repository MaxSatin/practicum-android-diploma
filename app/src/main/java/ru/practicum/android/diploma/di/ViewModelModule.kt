package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.presentation.viewmodel.FavoriteScreenViewModel
import ru.practicum.android.diploma.filter.presentation.viewmodel.FilterCountriesViewModel
import ru.practicum.android.diploma.filter.presentation.viewmodel.FilterIndustryViewModel
import ru.practicum.android.diploma.filter.presentation.viewmodel.FilterPlaceOfWorkViewModel
import ru.practicum.android.diploma.filter.presentation.viewmodel.FilterSettingsViewModel
import ru.practicum.android.diploma.filter.presentation.viewmodel.RegionFilterViewModel
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchViewModel
import ru.practicum.android.diploma.vacancy.presentation.VacancyDetailsViewModel

val viewModelModule = module {

    viewModel {
        FavoriteScreenViewModel(get(), androidContext())
    }
    viewModel {
        FilterIndustryViewModel(get(), get())
    }

    viewModel {
        FilterPlaceOfWorkViewModel(get(), get())
    }

    viewModel {
        FilterCountriesViewModel(get(), get())
    }

    viewModel { (vacancyId: String, isFromFavoritesScreen: Boolean) ->
        VacancyDetailsViewModel(vacancyId, get(), get(), get(), isFromFavoritesScreen)
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }

    viewModel {
        FilterSettingsViewModel(get())
    }

    viewModel {
        FilterCountriesViewModel(get(), get())
    }
    viewModel {
        RegionFilterViewModel(get(), get())
    }

}
