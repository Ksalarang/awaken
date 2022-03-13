package com.diyartaikenov.app.awaken.ui.presets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.diyartaikenov.app.awaken.BaseApplication
import com.diyartaikenov.app.awaken.MainActivity
import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.FragmentPresetsBinding
import com.diyartaikenov.app.awaken.model.MeditationPreset
import com.diyartaikenov.app.awaken.ui.adapter.PresetListAdapter
import com.diyartaikenov.app.awaken.ui.viewmodel.PresetViewModel
import com.diyartaikenov.app.awaken.ui.viewmodel.PresetViewModelFactory
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionViewModel
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionViewModelFactory

/**
 * A [Fragment] to view the list of [MeditationPreset]s stored in the database.
 * Tap the [FloatingActionButton] to add a new [MeditationPreset]
 * Tap any preset item or the 'Meditate' button to launch a new session
 */
class PresetsFragment: Fragment() {

    private val presetViewModel: PresetViewModel by activityViewModels {
        PresetViewModelFactory(
            (activity?.application as BaseApplication).database.meditationPresetDao()
        )
    }

    private val sessionViewModel: SessionViewModel by activityViewModels {
        SessionViewModelFactory(
            (activity?.application as BaseApplication).database.meditationSessionDao()
        )
    }

    private var _binding: FragmentPresetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPresetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        val adapter = PresetListAdapter(
            { preset ->
                // A click listener to open the session fragment
                findNavController().navigate(
                    PresetsFragmentDirections.actionNavPresetsToNavSession(preset.durationInMinutes)
                )
            },
            { preset ->
                // A click listener to edit the preset
                mainActivity.setBottomNavigationVisibility(View.GONE)
                findNavController().navigate(
                    PresetsFragmentDirections.actionNavPresetsToNavAddPreset(preset.id)
                )
            },
            { preset ->
                // A click listener to delete the preset
                presetViewModel.deletePreset(preset)
            }
        )

        presetViewModel.presets.observe(viewLifecycleOwner) { presets ->
            adapter.submitList(presets)
        }

        binding.apply {
            recyclerView.adapter = adapter
            fabAddMeditationPreset.setOnClickListener {
                mainActivity.setBottomNavigationVisibility(View.GONE)
                findNavController().navigate(R.id.action_nav_presets_to_nav_add_preset)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.setBottomNavigationVisibility(View.VISIBLE)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
