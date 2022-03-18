package com.diyartaikenov.app.awaken.ui.presets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.diyartaikenov.app.awaken.BaseApplication
import com.diyartaikenov.app.awaken.MainActivity
import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.FragmentPresetsBinding
import com.diyartaikenov.app.awaken.model.MeditationPreset
import com.diyartaikenov.app.awaken.ui.adapter.PresetListAdapter
import com.diyartaikenov.app.awaken.ui.presets.session.SessionActivity
import com.diyartaikenov.app.awaken.ui.viewmodel.PresetViewModel
import com.diyartaikenov.app.awaken.ui.viewmodel.PresetViewModelFactory
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionViewModel
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionViewModelFactory

const val EXTRA_INITIAL_DURATION = "INITIAL_DURATION"
const val EXTRA_ACTUAL_DURATION = "ACTUAL_DURATION"
const val EXTRA_END_TIMESTAMP = "END_TIMESTAMP"
const val MILLIS_IN_SECOND = 1000L
const val RESULT_CODE_OK = 20

private const val REQUEST_CODE = 1

/**
 * A [Fragment] to view the list of [MeditationPreset]s stored in the database.
 * Tap the [FloatingActionButton] to add a new [MeditationPreset]
 * Tap the 'Meditate' button to launch a new session
 */
@Suppress("DEPRECATION")
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

    private var sessionStartTimestamp: Long = 0
    private var sessionEndTimestamp: Long = 0

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
            { preset -> // Click listener to open the Session activity
                sessionStartTimestamp = System.currentTimeMillis() / MILLIS_IN_SECOND

                val startSessionActivity = Intent(context, SessionActivity::class.java)
                    .putExtra(EXTRA_INITIAL_DURATION, preset.durationInMinutes)

                startActivityForResult(startSessionActivity, REQUEST_CODE)
            },
            { preset -> // Click listener to edit the preset
                mainActivity.setBottomNavigationVisibility(View.GONE)
                findNavController().navigate(
                    PresetsFragmentDirections.actionNavPresetsToNavAddPreset(preset.id)
                )
            },
            { preset -> // Click listener to delete the preset
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_CODE_OK && data != null) {
            val sessionDuration = data.getIntExtra(EXTRA_ACTUAL_DURATION, 0)
            sessionEndTimestamp = data.getLongExtra(EXTRA_END_TIMESTAMP, 0)

            sessionViewModel.addSession(
                sessionDuration,
                sessionStartTimestamp,
                sessionEndTimestamp
            )

            Toast.makeText(
                context,
                "$sessionDuration min saved",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
