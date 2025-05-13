package com.almadistefano.finantrack.ui.fragments.home

import RemoteDataSource
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.adapters.CuentaAdapter
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentHomeBinding
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Transaccion
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var repository: Repository
    private lateinit var vm: HomeViewModel

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: CuentaAdapter
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = requireActivity().application as FinantrackApplication

        val userId = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("usuario_id", -1)

        repository = Repository(
            LocalDataSource(
                app.appDB.cuentaDao(),
                app.appDB.categoriaDao(),
                app.appDB.presupuestoDao(),
                app.appDB.transaccionDao(),
                app.appDB.usuarioDao()
            ),
            RemoteDataSource()
        )

        val factory = HomeViewModelFactory(repository, userId)
        vm = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onResume() {
        super.onResume()

        val cuentaId = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("cuenta_id", -1)

        vm.fetchTransacciones(cuentaId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CuentaAdapter(emptyList(), object : CuentaAdapter.OnCuentaSelectedListener {
            override fun onCuentaSelected(cuenta: Cuenta) {
                val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit {
                    putInt("cuenta_id", cuenta.id)
                }
            }
        })

        observeCuentas()

        val cuentaId = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("cuenta_id", -1)

        vm.fetchTransacciones(cuentaId)

        observeTransacciones()
    }

    private fun observeCuentas() {
        lifecycleScope.launch {
            vm.cuentas.collect { cuentas ->
                adapter.updateData(cuentas)

                val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val cuentaGuardada = prefs.getInt("cuenta_id", -1)

                if (cuentaGuardada == -1 && cuentas.isNotEmpty()) {
                    val cuentaPorDefecto = cuentas.first()
                    prefs.edit {
                        putInt("cuenta_id", cuentaPorDefecto.id)
                    }
                }
            }
        }
    }

    private fun observeTransacciones() {
        lifecycleScope.launch {
            vm.transacciones.collect { lista ->
                _binding?.let { binding ->

                    val gastos = lista.filter { it.tipo == "gasto" }
                    val ingresos = lista.filter { it.tipo == "ingreso" }

                    val totalIngresos = ingresos.sumOf { it.monto }
                    val totalGastos = gastos.sumOf { it.monto }
                    val balance = totalIngresos - totalGastos

                    binding.tvBalanceNumero.text = "%.2f €".format(balance)

                    if (balance >= 0) {
                        binding.tvEstadoBalance.text = "POSITIVO"
                        binding.tvEstadoBalance.setTextColor(Color.parseColor("#2E7D32"))
                        binding.tvBalanceNumero.setTextColor(Color.parseColor("#2E7D32"))
                    } else {
                        binding.tvEstadoBalance.text = "NEGATIVO"
                        binding.tvEstadoBalance.setTextColor(Color.parseColor("#B71C1C"))
                        binding.tvBalanceNumero.setTextColor(Color.parseColor("#B71C1C"))
                    }

                    binding.tvTotalGastos.text = "Total gastos: %.2f €".format(totalGastos)
                    binding.tvTotalIngresos.text = "Total ingresos: %.2f €".format(totalIngresos)

                    val categoriasMap = vm.categorias.value

                    fun calcularPorcentajes(data: List<Transaccion>): List<PieEntry> {
                        val totales = data.groupBy { it.categoriaId }.mapValues { it.value.sumOf { t -> t.monto } }
                        val total = totales.values.sum()
                        return totales.map {
                            val nombre = categoriasMap[it.key] ?: "Categoría ${it.key}"
                            PieEntry((it.value / total * 100).toFloat(), nombre)
                        }
                    }

                    val entriesGastos = calcularPorcentajes(gastos)
                    val entriesIngresos = calcularPorcentajes(ingresos)

                    cargarGrafico(binding.chartGastos, entriesGastos, "Gastos", Color.RED)
                    cargarGrafico(binding.chartIngresos, entriesIngresos, "Ingresos", Color.GREEN)
                }
            }
        }


}

    private fun cargarGrafico(chart: PieChart, entries: List<PieEntry>, label: String, color: Int) {
        val dataSet = PieDataSet(entries, label)
        dataSet.sliceSpace = 2f
        dataSet.valueTextSize = 12f
        dataSet.colors = listOf(color)

        val data = PieData(dataSet)

        chart.data = data
        chart.description.isEnabled = false
        chart.setUsePercentValues(true)
        chart.setDrawHoleEnabled(true)
        chart.setHoleColor(Color.WHITE)
        chart.setEntryLabelColor(Color.BLACK)
        chart.setCenterText(label)
        chart.animateY(1000, Easing.EaseInOutQuad)
        chart.invalidate()
    }
}
