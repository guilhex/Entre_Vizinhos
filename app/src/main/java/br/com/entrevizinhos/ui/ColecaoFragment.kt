package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.entrevizinhos.databinding.FragmentColecaoBinding
import br.com.entrevizinhos.ui.adapter.AnuncioAdapter
import br.com.entrevizinhos.viewmodel.LerAnuncioViewModel

/**
 * Fragment que exibe a coleção de anúncios favoritados pelo usuário
 * Filtra automaticamente apenas os itens marcados como favoritos
 */
class ColecaoFragment : Fragment() {
    private var bindingNullable: FragmentColecaoBinding? = null // Binding nullável para cleanup
    private val binding get() = bindingNullable!! // Acesso seguro ao binding

    // ViewModel compartilhado para manter consistência com outras telas
    private val lerAnuncioViewModel: LerAnuncioViewModel by activityViewModels()

    private lateinit var adapter: AnuncioAdapter // Adapter para RecyclerView

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
        setupRecyclerView() // Configura lista de favoritos
        setupObservers() // Observa mudanças nos dados
    }

    // Configura RecyclerView para exibir apenas favoritos
    private fun setupRecyclerView() {
        adapter =
            AnuncioAdapter(
                listaAnuncios = emptyList(), // Lista inicial vazia
                favoritosIds = lerAnuncioViewModel.favoritosIds.value ?: emptySet(), // Cache de favoritos
                onAnuncioClick = { anuncio ->
                    // Navegação para tela de detalhes do anúncio
                    val action = ColecaoFragmentDirections.actionColecaoToDetalhesAnuncio(anuncio)
                    findNavController().navigate(action)
                },
                onFavoritoClick = { anuncio ->
                    // Remove/adiciona favorito via ViewModel compartilhado
                    lerAnuncioViewModel.onFavoritoClick(anuncio.id)
                },
            )

        // Layout vertical para melhor visualização dos favoritos
        binding.rvColecao.layoutManager = LinearLayoutManager(requireContext())
        binding.rvColecao.adapter = adapter
    }

    // Observa mudanças nos dados e aplica filtro de favoritos
    private fun setupObservers() {
        // Observer 1: Quando lista completa de anúncios muda
        lerAnuncioViewModel.anuncios.observe(viewLifecycleOwner) { lista ->
            val favoritos = lerAnuncioViewModel.favoritosIds.value ?: emptySet() // IDs favoritados
            val listaFiltrada = lista.filter { it.id in favoritos } // Filtra apenas favoritos
            adapter.atualizarLista(listaFiltrada, favoritos) // Atualiza UI
        }

        // Observer 2: Quando lista de favoritos muda (adicionar/remover)
        lerAnuncioViewModel.favoritosIds.observe(viewLifecycleOwner) { favoritos ->
            val lista = lerAnuncioViewModel.anuncios.value ?: emptyList() // Lista completa
            val listaFiltrada = lista.filter { it.id in favoritos } // Reaplica filtro
            adapter.atualizarLista(listaFiltrada, favoritos) // Atualiza UI com novos favoritos
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingNullable = null
    }
}
