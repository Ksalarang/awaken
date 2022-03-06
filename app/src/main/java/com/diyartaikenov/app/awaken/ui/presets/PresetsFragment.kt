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

/**
 * A [Fragment] to view the list of [MeditationPreset]s stored in the database.
 * Tap the [FloatingActionButton] to add a new [MeditationPreset]
 */
class PresetsFragment: Fragment() {

    private val viewModel: PresetViewModel by activityViewModels {
        PresetViewModelFactory(
            (activity?.application as BaseApplication).database.meditationPresetDao()
        )
    }

    private var _binding: FragmentPresetsBinding? = null
    private val binding get() = _binding!!

    private var _mainActivity: MainActivity? = null
    private val mainActivity get() = _mainActivity!!

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

        _mainActivity = requireActivity() as MainActivity

        val adapter = PresetListAdapter(
            { preset ->
                // A click listener to open the session fragment
                mainActivity.setBottomNavigationVisibility(View.GONE)
                findNavController().navigate(R.id.action_nav_presets_to_nav_session)
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
                viewModel.deletePreset(preset)
            }
        )

        viewModel.presets.observe(viewLifecycleOwner) { presets ->
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
        _mainActivity = null
        super.onDestroyView()
    }
}
