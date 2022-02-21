package com.diyartaikenov.app.awaken.ui.presets

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diyartaikenov.app.awaken.BaseApplication
import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.FragmentAddPresetBinding
import com.diyartaikenov.app.awaken.databinding.FragmentPresetsBinding
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

        binding.fabSavePreset.setOnClickListener {
            val meditationName = binding.nameInput.text.toString()
            if (meditationName.isEmpty()) {
                binding.nameInputLayout.error = getString(R.string.name_input_error_message)
            } else {
                binding.nameInputLayout.error = null
            }
        }

        // The value of the duration EditText cannot be less than 1.
        binding.duration.addTextChangedListener { editable ->
            val text = editable?.toString()
            if (text.isNullOrEmpty() || text == "0") {
                binding.duration.setText("1")
                binding.duration.selectAll()
            }
        }
    }
}