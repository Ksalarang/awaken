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

        hideAllBars()
    }

    override fun onDestroyView() {
        showAllBars()
        windowInsetsController = null
        _binding = null
        super.onDestroyView()
    }

    /**
     * Hide all the system bars and the action bar
     */
    private fun hideAllBars() {
        // Configure the behavior of the hidden system bars
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
        // Hide the action bar
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    /**
     * Show all the system bars and the navigation bar.
     */
    private fun showAllBars() {
        // Show all the system bars
        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
        // Show the action bar
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}