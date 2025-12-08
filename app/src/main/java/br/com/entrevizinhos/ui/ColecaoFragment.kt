package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.entrevizinhos.databinding.FragmentColecaoBinding
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.ui.adapter.AnuncioAdapter
import br.com.entrevizinhos.viewmodel.AnuncioViewModel

class ColecaoFragment : Fragment() {
    private var bindingNullable: FragmentColecaoBinding? = null
    private val binding get() = bindingNullable!!

    // Compartilha o ViewModel com outras telas
    private val anuncioViewModel: AnuncioViewModel by activityViewModels()

    private lateinit var adapter: AnuncioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingNullable = FragmentColecaoBinding.inflate(inflater, container, false)
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
        adapter =
            AnuncioAdapter(
                listaAnuncios = emptyList(),
                favoritosIds = anuncioViewModel.favoritosIds.value ?: emptySet(),
                onAnuncioClick = { anuncio ->
                    Toast.makeText(requireContext(), "Abrir detalhes: ${anuncio.titulo}", Toast.LENGTH_SHORT).show()
                },
                onFavoritoClick = { anuncio ->
                    // Delegar toggle para o ViewModel
                    anuncioViewModel.onFavoritoClick(anuncio.id)
                },
            )

        binding.rvColecao.layoutManager = LinearLayoutManager(requireContext())
        binding.rvColecao.adapter = adapter
    }

    private fun setupObservers() {
        // Observa anúncios e favoritos e mostra apenas os favoritos
        anuncioViewModel.anuncios.observe(viewLifecycleOwner) { lista ->
            val favoritos = anuncioViewModel.favoritosIds.value ?: emptySet()
            val listaFiltrada = lista.filter { it.id in favoritos }
            adapter.atualizarLista(listaFiltrada, favoritos)
        }

        anuncioViewModel.favoritosIds.observe(viewLifecycleOwner) { favoritos ->
            val lista = anuncioViewModel.anuncios.value ?: emptyList()
            val listaFiltrada = lista.filter { it.id in favoritos }
            adapter.atualizarLista(listaFiltrada, favoritos)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingNullable = null
    }
}
