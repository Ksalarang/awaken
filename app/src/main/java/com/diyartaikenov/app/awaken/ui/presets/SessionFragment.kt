package com.diyartaikenov.app.awaken.ui.presets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.diyartaikenov.app.awaken.databinding.FragmentSessionBinding

/**
 * This fragment is showed during a meditation session.
 */
class SessionFragment: Fragment() {

    private var _binding: FragmentSessionBinding? = null
    private val binding get() = _binding!!

    private var windowInsetsController: WindowInsetsControllerCompat? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        windowInsetsController =
            ViewCompat.getWindowInsetsController(requireActivity().window.decorView)

        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onDestroyView() {
        (activity as AppCompatActivity).supportActionBar?.show()
        windowInsetsController = null
        _binding = null
        super.onDestroyView()
    }

    /**
     * Hide the status and navigation bars.
     */
    private fun hideSystemBars() {
        // Configure the behavior of the hidden system bars
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Hide both the status bar and the navigation bar
        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
    }

    /**
     * Show the status and navigation bars.
     */
    private fun showSystemBars() {
        // Show all the system bars
        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
    }
}
