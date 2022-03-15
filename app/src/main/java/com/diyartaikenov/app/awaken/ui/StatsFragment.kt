package com.diyartaikenov.app.awaken.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diyartaikenov.app.awaken.BaseApplication
import com.diyartaikenov.app.awaken.databinding.FragmentStatsBinding
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionViewModel
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class StatsFragment: Fragment() {

    private val sessionViewModel: SessionViewModel by activityViewModels {
        SessionViewModelFactory(
            (activity?.application as BaseApplication).database.meditationSessionDao()
        )
    }

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var chart: LineChart

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

        chart = binding.lineChart

        val lineDataSet = LineDataSet(entryList(), "")

        chart.data = LineData(lineDataSet)
        chart.setVisibleXRangeMaximum(7F)
        chart.invalidate()
    }

    private fun entryList(): List<Entry> {
        return listOf(
            Entry(1F, 5F),
            Entry(2F, 10F),
            Entry(3F, 15F),
            Entry(4F, 20F),
            Entry(5F, 10F),
            Entry(6F, 30F),
            Entry(7F, 10F),
        )
    }
}
