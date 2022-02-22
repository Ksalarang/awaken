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
                }
            }

            duration.addAfterTextChangedListener()
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