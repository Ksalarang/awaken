package com.diyartaikenov.app.awaken.ui.presets

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
class AddPresetFragment : Fragment(R.layout.fragment_add_preset) {

    private val viewModel: PresetViewModel by activityViewModels {
        PresetViewModelFactory(
            (activity?.application as BaseApplication).database.meditationPresetDao()
        )
    }

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

        binding.apply {
            fabSavePreset.setOnClickListener {
                val meditationName = nameInput.text.toString()
                if (meditationName.isEmpty()) {
                    nameInputLayout.error = getString(R.string.name_input_error_message)
                } else {
                    nameInputLayout.error = null
                    viewModel.addPreset(meditationName, duration.text.toString().toInt())
                    findNavController().navigate(
                        R.id.action_nav_add_preset_to_nav_presets
                    )
                }
            }

            duration.addAfterTextChangedListener()

            // subtract 5 from the duration editText value
            subtractDurationButton.setOnClickListener {
                val value = duration.text.toString().toInt() - 5
                if (value > 0) {
                    duration.setText(value.toString())
                }
            }
            // add 5 to the duration editText value
            addDurationButton.setOnClickListener {
                val value = duration.text.toString().toInt() + 5
                if (value < 1000) {
                    duration.setText(value.toString())
                }
            }
        }
    }

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
}