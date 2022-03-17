package com.diyartaikenov.app.awaken.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.diyartaikenov.app.awaken.BaseApplication
import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.FragmentStatsBinding
import com.diyartaikenov.app.awaken.model.MeditationSession
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionViewModel
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionViewModelFactory

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

class StatsFragment: Fragment() {

    private val sessionViewModel: SessionViewModel by activityViewModels {
        SessionViewModelFactory(
            (activity?.application as BaseApplication).database.meditationSessionDao()
        )
    }

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var chart: BarChart

    private lateinit var sessions: List<MeditationSession>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionViewModel.sessions.observe(viewLifecycleOwner) {
            this.sessions = it
        }

        chart = binding.barChart

        val barDataSet = BarDataSet(entries(), "")

        chart.apply {
            data = BarData(barDataSet)
            data.setDrawValues(false)

            description.isEnabled = false
            legend.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1F
                valueFormatter = DayAxisValueFormatter()
            }

            axisLeft.setDrawAxisLine(false)

            axisRight.apply {
                setDrawLabels(false)
                setDrawAxisLine(false)
            }

            invalidate()
        }
    }

    private fun entries(): List<BarEntry> {
        return listOf(
            BarEntry(1F, 6F),
            BarEntry(2F, 10F),
            BarEntry(3F, 15F),
            BarEntry(4F, 19F),
            BarEntry(5F, 10F),
            BarEntry(6F, 30F),
            BarEntry(7F, 14F),
        )
    }
}
