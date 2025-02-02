package ru.practicum.android.diploma.filter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding

class FilterSettingsFragment : Fragment() {
    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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

        binding.topBar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.inputSalary.doAfterTextChanged { text ->
            binding.clearSalaryButton.isVisible = text?.isBlank() != true
        }

        binding.inputSalary.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            binding.hintTitle.isActivated = true
        }

        binding.clearSalaryButton.setOnClickListener {
            binding.inputSalary.setText(null.orEmpty())
            binding.inputSalary.requestFocus()
        }

        binding.workplaceContainer.setOnClickListener {
            findNavController().navigate(
                R.id.action_filterSettingsFragment_to_filterPlaceOfWorkFragment
            )
        }

        binding.industryContainer.setOnClickListener {
            findNavController().navigate(
                R.id.action_filterSettingsFragment_to_filterIndustryFragment
            )
        }

    }

}
