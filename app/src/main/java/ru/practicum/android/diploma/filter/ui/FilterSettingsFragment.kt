package ru.practicum.android.diploma.filter.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.sharedprefs.models.City
import ru.practicum.android.diploma.common.sharedprefs.models.Country
import ru.practicum.android.diploma.common.sharedprefs.models.Filter
import ru.practicum.android.diploma.common.sharedprefs.models.IndustrySP
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.filter.presentation.viewmodel.FilterSettingsViewModel

class FilterSettingsFragment : Fragment() {

    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FilterSettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshUpdatedFilter()
        val currentFilter = viewModel.currentFilter.value
        println("Currnet  Filter onViewCreated: $currentFilter")

        setupWorkplaceUI(currentFilter?.areaCountry, currentFilter?.areaCity)
        setupIndustryUI(currentFilter?.industrySP)
        setupSalary(currentFilter?.salary)
        setupSalaryCheckbox(currentFilter?.withSalary)

        viewModel.updatedFilter.observe(viewLifecycleOwner) { updatedFilter ->
            binding.submitButton.isVisible = updatedFilter != currentFilter
            binding.resetButton.isVisible = updatedFilter != Filter()
        }
        setupListeners()
    }

    override fun onResume() {
        super.onResume()

        viewModel.refreshUpdatedFilter()

        val currentFilter = viewModel.currentFilter.value
        val updatedFilter = viewModel.updatedFilter.value

        setupWorkplaceUI(updatedFilter?.areaCountry, updatedFilter?.areaCity)
        setupIndustryUI(updatedFilter?.industrySP)
        setupSalary(updatedFilter?.salary)
        setupSalaryCheckbox(updatedFilter?.withSalary)

        viewModel.updatedFilter.observe(viewLifecycleOwner) { updatedFilter ->
            binding.submitButton.isVisible = updatedFilter != currentFilter
            binding.resetButton.isVisible = updatedFilter != Filter()
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.topBar.setOnClickListener { findNavController().navigateUp() }
        binding.workplaceContainer.setOnClickListener { navigateToPlaceOfWorkFragment() }
        binding.industryContainer.setOnClickListener { navigateToIndustryFragment() }
        binding.resetButton.setOnClickListener { resetButtonClickListener() }
        setupInputSalaryListeners()
        binding.submitButton.setOnClickListener { submitFilter() }
    }

    // WORKPLACE
    private fun setupWorkplaceUI(country: Country?, city: City?) {
        updateWorkplaceUI(country, city) { country, city ->
            binding.workplaceBtn.setOnClickListener {
                viewModel.clearFilterField("areaCountry")
                viewModel.clearFilterField("areaCity")
                updateWorkplaceUI(null, null)
            }
        }
    }

    private fun updateWorkplaceUI(country: Country?, city: City?, onClear: ((Country?, City?) -> Unit)? = null) {
        val workplaceText = when {
            country != null && city != null -> "${country.name}, ${city.name}"
            country != null -> country.name
            city != null -> city.name
            else -> null
        }
        with(binding) {
            workplaceValue.text = workplaceText
            workplaceValue.visibility = if (workplaceText != null) View.VISIBLE else View.GONE
            workplaceBtn.setImageResource(
                if (workplaceText != null) R.drawable.ic_close_24px else R.drawable.ic_arrow_forward_24px
            )
            workplacePlaceholder.isVisible = workplaceText == null
            workplaceLabel.isVisible = workplaceText != null
            if (workplaceText == null) {
                workplaceBtn.setOnClickListener { navigateToPlaceOfWorkFragment() }
            } else {
                onClear?.invoke(country, city)
            }
        }
    }

    // INDUSTRY
    private fun setupIndustryUI(industrySP: IndustrySP?) {
        updateIndustryUI(industrySP) { industry ->
            binding.industryBtn.setOnClickListener {
                viewModel.clearFilterField("industrySP")
                updateIndustryUI(null)
            }
        }
    }

    private fun updateIndustryUI(industrySP: IndustrySP?, onClear: ((IndustrySP?) -> Unit)? = null) {
        val industryText = industrySP?.name
        with(binding) {
            industryValue.text = industryText
            industryValue.visibility = if (industryText != null) View.VISIBLE else View.GONE
            industryBtn.setImageResource(
                if (industryText != null) R.drawable.ic_close_24px else R.drawable.ic_arrow_forward_24px
            )
            industryPlaceholder.isVisible = industryText == null
            industryLabel.isVisible = industryText != null
            if (industryText == null) {
                industryBtn.setOnClickListener { navigateToIndustryFragment() }
            } else {
                onClear?.invoke(industrySP)
            }
        }
    }

    // SALLARY EDIT TEXT
    private fun setupSalary(salary: Int?) {
        salary?.let { binding.inputSalary.setText(it.toString()) }

        binding.inputSalary.doAfterTextChanged { text ->
            viewModel.updateFilter(Filter(salary = text.toString().toIntOrNull()))
        }

        binding.clearSalaryButton.setOnClickListener {
            binding.inputSalary.setText("")
            binding.inputSalary.clearFocus()
            hideKeyboard(binding.root, requireContext())
            viewModel.clearFilterField("salary")
        }

        binding.inputSalary.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(binding.root, requireContext())
                binding.inputSalary.clearFocus()
                true
            }
            false
        }

    }

    private fun setupInputSalaryListeners() {
        var isFocused = false

        binding.inputSalary.doAfterTextChanged { text ->
            if (text != null) {
                val newSalary = text.toString().toIntOrNull()
                val currentSalary = viewModel.currentFilter.value?.salary

                if (newSalary != currentSalary) {
                    viewModel.updateFilter(Filter(salary = newSalary))
                }

                binding.clearSalaryButton.isVisible = isFocused && text.isNotBlank()
            }
        }

        binding.inputSalary.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            isFocused = hasFocus
            binding.hintTitle.isActivated = hasFocus
            binding.clearSalaryButton.isVisible = hasFocus && binding.inputSalary.text?.isNotBlank() == true
        }
    }

    // WITH SALLARY CHECKBOX
    private fun setupSalaryCheckbox(withSalary: Boolean?) {
        withSalary?.let { binding.checkBox.isChecked = it }

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.updateFilter(Filter(withSalary = true))
            } else {
                viewModel.clearFilterField("withSalary")
            }
        }
    }

    // RESET BUTTON
    private fun resetButtonClickListener() {
        viewModel.clearFilter()
        updateWorkplaceUI(null, null)
        updateIndustryUI(null)
        binding.inputSalary.text = null
        binding.checkBox.isChecked = false
        binding.submitButton.isVisible = false
        viewModel.refreshCurrentFilter()
        viewModel.refreshUpdatedFilter()
    }

    private fun navigateToPlaceOfWorkFragment() {
        findNavController().navigate(R.id.action_filterSettingsFragment_to_filterPlaceOfWorkFragment)
    }

    private fun navigateToIndustryFragment() {
        findNavController().navigate(R.id.action_filterSettingsFragment_to_filterIndustryFragment)
    }

    private fun submitFilter() {
        findNavController().navigateUp()
    }

    private fun hideKeyboard(view: View, context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
