package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.entrevizinhos.databinding.FragmentFeedBinding
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.ui.adapter.AnuncioAdapter
import br.com.entrevizinhos.ui.viewModel.AnuncioViewModel

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null

    private val anuncioViewModel: AnuncioViewModel by viewModels() // by serve para a criação da val esta de responsa do viewmodel, ele verifica se ja existe na memoria antes de criar

    private lateinit var adapter: AnuncioAdapter // lateintit permite ser vazio
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        // Dados de teste
//        val listaTeste =
//            listOf(
//                Anuncio(id = "1", titulo = "Bicicleta Caloi", preco = 450.00, cidade = "Centro"),
//                Anuncio(id = "2", titulo = "Sofá 3 lugares", preco = 200.00, cidade = "Vila Nova"),
//                Anuncio(id = "3", titulo = "Geladeira", preco = 800.00, cidade = "Centro"),
//            )
        adapter =
            AnuncioAdapter(emptyList()) { anuncio ->
                Toast.makeText(requireContext(), "Clicou: ${anuncio.titulo}", Toast.LENGTH_SHORT).show()
            }

        binding.rvAnuncios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnuncios.adapter = adapter
    }

    private fun setupObservers() {
        // observe fica de olho se teve alteração no banco, se sim chama para atualizar a lista
        anuncioViewModel.anuncios.observe(viewLifecycleOwner) { listaAnuncios ->
            adapter.atualizarLista(listaAnuncios)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
