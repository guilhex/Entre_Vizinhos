package br.com.entrevizinhos.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.entrevizinhos.R
import br.com.entrevizinhos.databinding.FragmentFeedBinding
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.ui.adapter.AnuncioAdapter
import br.com.entrevizinhos.viewmodel.AnuncioViewModel
import com.google.android.material.chip.Chip

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null

    private val binding get() = _binding!!

    private val anuncioViewModel: AnuncioViewModel by viewModels()

    private lateinit var adapter: AnuncioAdapter

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
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        capturaCategoria()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter =
            AnuncioAdapter(emptyList()) { anuncio ->
                val action = FeedFragmentDirections.actionFeedFragmentToDetalhesAnuncioFragment(anuncio)
                findNavController().navigate(action)
            }

        binding.rvAnuncios.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAnuncios.adapter = adapter
    }

    private fun setupObservers() {
        binding.pbLoading.visibility = View.VISIBLE

        anuncioViewModel.anuncios.observe(viewLifecycleOwner) { listaAnuncios ->
            binding.pbLoading.visibility = View.GONE

            aplicarFiltros(listaAnuncios, getString(R.string.categoria_todos))

            if (listaAnuncios.isEmpty()) {
                Toast.makeText(requireContext(), "Nenhum anúncio encontrado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun aplicarFiltros(
        listaCompleta: List<Anuncio>,
        categoria: String,
    ) {
        val labelTodos = getString(R.string.categoria_todos)
        var listaFiltrada =
            if (categoria.equals(labelTodos, ignoreCase = true)) {
                listaCompleta
            } else {
                listaCompleta.filter { anuncio ->
                    anuncio.categoria.equals(categoria, ignoreCase = true)
                }
            }

        adapter.atualizarLista(listaFiltrada)
    }

    private fun capturaCategoria() {
        binding.chipGroupCategorias.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener

            val chipSelecionado = group.findViewById<Chip>(checkedId)

            val categoriaSelecionada = chipSelecionado.text.toString()

            val listaAtual = anuncioViewModel.anuncios.value ?: emptyList()

            aplicarFiltros(listaAtual, categoriaSelecionada)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}