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

    private lateinit var binding: FragmentPresetsBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPresetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        val adapter = PresetListAdapter(
            { preset ->
                // TODO: Start meditation session click listener
            },
            { preset ->
                // Edit preset click listener
                mainActivity.setBottomNavigationVisibility(View.GONE)
                findNavController().navigate(
                    PresetsFragmentDirections.actionNavPresetsToNavAddPreset(preset.id)
                )
            },
            { preset ->
                // Delete preset click listener
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

                findNavController().navigate(
                    R.id.action_nav_presets_to_nav_add_preset
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.setBottomNavigationVisibility(View.VISIBLE)
    }
}