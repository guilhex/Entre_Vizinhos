package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import br.com.entrevizinhos.R
import br.com.entrevizinhos.databinding.ActivityMainBinding

/**
 * Activity principal que gerencia a navegação entre fragments
 * Contém NavHostFragment e BottomNavigationView
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // View Binding para acesso às views

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configura View Binding para evitar findViewById
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Define layout da activity

        // Obtém referência ao NavHostFragment (container dos fragments)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController // Controlador de navegação

        // Sincroniza BottomNavigationView com Navigation Component
        binding.navView.setupWithNavController(navController)

        // Listener para controlar visibilidade da bottom navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Esconde barra em telas que não precisam dela
            if (destination.id == R.id.loginFragment || destination.id == R.id.criarAnuncioFragment) {
                binding.navView.visibility = View.GONE // Tela cheia
            } else {
                binding.navView.visibility = View.VISIBLE // Mostra navegação
            }
        }
    }
}
