package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.entrevizinhos.databinding.FragmentColecaoBinding
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.ui.adapter.AnuncioAdapter

class ColecaoFragment : Fragment() {
    private var _binding: FragmentColecaoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentColecaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Simulação: Apenas 1 item favoritado
        val listaFavoritos =
            listOf(
                Anuncio(id = "1", titulo = "Bicicleta Caloi", preco = 450.00, cidade = "Centro"),
            )

        val adapter =
            AnuncioAdapter(listaFavoritos) { anuncio ->
                Toast.makeText(requireContext(), "Abrir detalhes: ${anuncio.titulo}", Toast.LENGTH_SHORT).show()
            }

        binding.rvColecao.layoutManager = LinearLayoutManager(requireContext())
        binding.rvColecao.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
