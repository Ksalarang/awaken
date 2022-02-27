package com.diyartaikenov.app.awaken.ui.presets

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diyartaikenov.app.awaken.BaseApplication
import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.FragmentAddPresetBinding
import com.diyartaikenov.app.awaken.model.MeditationPreset
import com.diyartaikenov.app.awaken.ui.viewmodel.PresetViewModel
import com.diyartaikenov.app.awaken.ui.viewmodel.PresetViewModelFactory

/**
 * A [Fragment] to enter data for a new [MeditationPreset] or to edit data for
 * an existing [MeditationPreset].
 */
class AddPresetFragment : Fragment() {

    private val viewModel: PresetViewModel by activityViewModels {
        PresetViewModelFactory(
            (activity?.application as BaseApplication).database.meditationPresetDao()
        )
    }

    private val navArgs: AddPresetFragmentArgs by navArgs()

    private lateinit var binding: FragmentAddPresetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPresetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val presetId = navArgs.id

        // Edit an existing preset
        if (presetId > 0) {
            viewModel.getPresetById(presetId).observe(viewLifecycleOwner) { preset ->
                bindPreset(preset)
            }

            binding.fabSavePreset.setOnClickListener {
                if (validatePresetName(nameInput())) {
                    viewModel.updatePreset(presetId, nameInput(), durationInput())
                    findNavController().navigate(R.id.action_nav_add_preset_to_nav_presets)
                }
            }
        }
        // Save a new preset
        else {
            binding.fabSavePreset.setOnClickListener {
                if (validatePresetName(nameInput())) {
                    viewModel.addPreset(nameInput(), durationInput())
                    findNavController().navigate(R.id.action_nav_add_preset_to_nav_presets)
                }
            }
            setDefaultName()
            binding.nameInput.requestFocus()
            requireActivity().showSoftInput(binding.nameInput)
        }

        // various listeners for duration edit text and its buttons
        binding.apply {
            duration.addAfterTextChangedListener()

            // subtract 5 from the duration editText value
            subtractDurationButton.setOnClickListener {
                val value = durationInput() - 5
                if (value > 0) {
                    duration.setText(value.toString())
                }
            }
            // add 5 to the duration editText value
            addDurationButton.setOnClickListener {
                val value = durationInput() + 5
                if (value < 1000) {
                    duration.setText(value.toString())
                }
            }
        }
    }

    /**
     * Show a [TextInputLayout] error and return false if the preset name is blank,
     * hide the error and return true otherwise.
     */
    private fun validatePresetName(presetName: String): Boolean {
        return if (presetName.isBlank()) {
            binding.nameInputLayout.error = getString(R.string.name_input_error_message)
            false
        } else {
            binding.nameInputLayout.error = null
            true
        }
    }

    /**
     * Fill in all the fields with a preset data.
     */
    private fun bindPreset(preset: MeditationPreset) {
        binding.apply {
            nameInput.setText(preset.name)
            duration.setText(preset.durationInMinutes.toString())
        }
    }

    /**
     * Provide a default name for a new meditation preset
     */
    private fun setDefaultName() {
        val presetAmount = viewModel.presets.value?.size ?: 0
        binding.nameInput.setText(getString(R.string.meditation_name, presetAmount + 1))
    }

    private fun nameInput() = binding.nameInput.text.toString()
    private fun durationInput() = binding.duration.text.toString().toInt()

    /**
     * Add an after text changed listener to this EditText which ensures that the value
     * is not empty or zero and set the value to 1 by default.
     * The inputType of this EditText is supposed to be 'number'.
     */
    private fun EditText.addAfterTextChangedListener(value: Int = 1) {
        this.addTextChangedListener { editable ->
            if (editable.isNullOrEmpty() || editable.toString() == "0") {
                this.setText("$value")
                this.selectAll()
            }
        }
    }

    private fun Activity.showSoftInput(view: View) {
        (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}
