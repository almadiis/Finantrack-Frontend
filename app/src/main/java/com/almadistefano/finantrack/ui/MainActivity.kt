package com.almadistefano.finantrack.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.databinding.ActivityMainBinding
import com.almadistefano.finantrack.ui.fragments.perfil.PerfilFragment
import com.almadistefano.finantrack.ui.fragments.presupuestos.PresupuestosFragment
import com.almadistefano.finantrack.ui.fragments.transacciones.TransaccionesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var perfilFragment: PerfilFragment
    private lateinit var presupuestosFragment: PresupuestosFragment
    private lateinit var transaccionesFragment: TransaccionesFragment
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Configura el diseño y la ventana para ajuste de sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bloquea la rotación de la pantalla
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        // Inicializar los fragmentos
        perfilFragment = PerfilFragment()
        presupuestosFragment = PresupuestosFragment()
        transaccionesFragment = TransaccionesFragment()


        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Vincula el bottom nav con el nav controller
        NavigationUI.setupWithNavController(bottomNav, navController)

    }

}