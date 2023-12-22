package com.example.rifsa_mobile.view.fragment.finance.financefragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rifsa_mobile.R
import com.example.rifsa_mobile.databinding.FragmentFinanceBinding
import com.example.rifsa_mobile.model.entity.remotefirebase.FinancialEntity
import com.example.rifsa_mobile.view.fragment.finance.FinanceViewModel
import com.example.rifsa_mobile.view.fragment.finance.adapter.FinanceRecyclerViewAdapter
import com.example.rifsa_mobile.viewmodel.viewmodelfactory.ViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class FinanceFragment : Fragment() {
    private lateinit var binding : FragmentFinanceBinding

    private val viewModel : FinanceViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }

    private var dataList = ArrayList<FinancialEntity>()
    private lateinit var lineChart: LineChart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinanceBinding.inflate(layoutInflater)

        val bottomMenu = requireActivity().findViewById<BottomNavigationView>(R.id.main_bottommenu)
        bottomMenu.visibility = View.VISIBLE

        lineChart = binding.imageNews
        setupLineChart()

        binding.fabFiannceInsert.setOnClickListener {
            findNavController().navigate(
                FinanceFragmentDirections.actionFinanceFragmentToFinanceInsertDetailFragment(
                    null
                )
            )
        }

        getFinanceList()

        return binding.root
    }


    private fun getFinanceList(){
        lifecycleScope.launch{
            try {
                viewModel.readFinancial().observe(viewLifecycleOwner){ data ->
                    showFinancialList(data)
                    dataChecker(data.size)
                }
            }catch (e : Exception){
                Log.d("FinanceFragment",e.toString())
            }
        }
    }

    private fun showFinancialList(data : List<FinancialEntity>){
        try {
            binding.pgbFinanceBar.visibility = View.GONE
            val adapter = FinanceRecyclerViewAdapter(data)
            val recyclerView = binding.rvFinance
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter.onItemCallBack(object : FinanceRecyclerViewAdapter.ItemDetailCallback{
                override fun onItemCallback(data: FinancialEntity) {
                    findNavController().navigate(
                        FinanceFragmentDirections.actionFinanceFragmentToFinanceInsertDetailFragment(data)
                    )
                }
            })
        }catch (e : Exception){
            Log.d("FinanceFragment",e.message.toString())
        }
    }

    private fun showStatus(title : String){
        binding.pgbKeaunganTitle.visibility = View.VISIBLE
        binding.pgbKeaunganTitle.text = title
        if (title.isNotEmpty()){
            binding.pgbFinanceBar.visibility = View.GONE
        }
        Log.d("FinanceFragment",title)
    }

    private fun dataChecker(total : Int){
        if (total == 0){
            binding.pgbFinanceBar.visibility = View.GONE
            binding.financeEmptyState.emptyState.visibility = View.VISIBLE
        }
    }
    private fun setupLineChart() {
        // Buat data set untuk chart
        val entries = ArrayList<Entry>()
        entries.add(Entry(1f, 50f))
        entries.add(Entry(2f, 80f))
        entries.add(Entry(3f, 60f))
        // Tambahkan data set ke dalam chart
        val dataSet = LineDataSet(entries, "Label Data Set")
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(dataSet)
        // Buat LineData dari data set
        val lineData = LineData(dataSets)
        // Atur LineData ke dalam LineChart
        lineChart.data = lineData

        // Konfigurasi lainnya seperti label dan legenda dapat ditambahkan sesuai kebutuhan
    }
}