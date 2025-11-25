package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.entrevizinhos.databinding.FragmentPerfilBinding

class PerfilFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        // Configurar clique nos botões
        binding.btnMeusAnuncios.setOnClickListener {
            Toast.makeText(requireContext(), "Ir para Meus Anúncios", Toast.LENGTH_SHORT).show()
        }

        binding.btnSair.setOnClickListener {
            // Futuramente aqui chamaremos: FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logout realizado", Toast.LENGTH_SHORT).show()

            // Lógica para voltar para tela de login viria aqui
        }

        // Simular carregamento de dados (Futuro Firebase)
        binding.tvNomeUsuario.text = "João da Silva"
        binding.tvEmailUsuario.text = "joao@gmail.com"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
