package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import br.com.entrevizinhos.R
import br.com.entrevizinhos.databinding.FragmentFeedBinding
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.ui.adapter.AnuncioAdapter
import br.com.entrevizinhos.viewmodel.LerAnuncioViewModel
import com.google.android.material.chip.Chip

/**
 * Fragment principal que exibe o feed de anúncios com filtros por categoria
 */
class FeedFragment : Fragment() {
    private var bindingNullable: FragmentFeedBinding? = null
    private val binding get() = bindingNullable!!

    // ViewModel compartilhado entre fragments
    private val lerAnuncioViewModel: LerAnuncioViewModel by activityViewModels()
    private lateinit var adapter: AnuncioAdapter

    // Variáveis para controle de filtros
    private var listaAtual: List<Anuncio> = emptyList()
    private var categoriaAtual: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingNullable = FragmentFeedBinding.inflate(inflater, container, false)
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

        // Define categoria inicial como "Todos"
        categoriaAtual = getString(R.string.categoria_todos)
    }

    // Configura toolbar de navegação
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Configura RecyclerView com adapter
    private fun setupRecyclerView() {
        adapter =
            AnuncioAdapter(
                listaAnuncios = emptyList(),
                favoritosIds = lerAnuncioViewModel.favoritosIds.value ?: emptySet(),
                onAnuncioClick = { anuncio ->
                    // Navega para detalhes do anúncio
                    val action =
                        FeedFragmentDirections.actionFeedFragmentToDetalhesAnuncioFragment(anuncio)
                    findNavController().navigate(action)
                },
                onFavoritoClick = { anuncio ->
                    // Alterna favorito via ViewModel
                    lerAnuncioViewModel.onFavoritoClick(anuncio.id)
                },
            )

        // Layout em grade com 2 colunas
        binding.rvAnuncios.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAnuncios.adapter = adapter
    }

    // Configura observadores do ViewModel
    private fun setupObservers() {
        // Mostra loading inicial
        binding.pbLoading.visibility = View.VISIBLE

        // Observa lista de anúncios
        lerAnuncioViewModel.anuncios.observe(viewLifecycleOwner) { listaAnuncios ->
            binding.pbLoading.visibility = View.GONE

            // Guarda lista e aplica filtros
            listaAtual = listaAnuncios
            aplicarFiltros(listaAnuncios, categoriaAtual.ifEmpty { getString(R.string.categoria_todos) })

            // Mostra mensagem se lista vazia
            if (listaAnuncios.isEmpty()) {
                Toast.makeText(requireContext(), "Nenhum anúncio encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        // Reaplica filtros quando favoritos mudarem
        lerAnuncioViewModel.favoritosIds.observe(viewLifecycleOwner) { _ ->
            aplicarFiltros(listaAtual, categoriaAtual.ifEmpty { getString(R.string.categoria_todos) })
        }
    }

    // Aplica filtro de categoria na lista
    private fun aplicarFiltros(
        listaCompleta: List<Anuncio>,
        categoria: String,
    ) {
        val labelTodos = getString(R.string.categoria_todos)
        
        // Filtra por categoria ou mostra todos
        val listaFiltrada =
            if (categoria.equals(labelTodos, ignoreCase = true)) {
                listaCompleta
            } else {
                listaCompleta.filter { anuncio ->
                    anuncio.categoria.equals(categoria, ignoreCase = true)
                }
            }

        // Atualiza adapter com lista filtrada
        adapter.atualizarLista(listaFiltrada, lerAnuncioViewModel.favoritosIds.value ?: emptySet())
    }

    // Configura listener para seleção de categoria
    private fun capturaCategoria() {
        binding.chipGroupCategorias.setOnCheckedStateChangeListener { group, checkedIds ->
            // Cancela se nenhum chip selecionado
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener

            // Encontra chip selecionado
            val chipSelecionado = group.findViewById<Chip>(checkedId)
            val categoriaSelecionada = chipSelecionado.text.toString()

            // Atualiza categoria atual
            categoriaAtual = categoriaSelecionada

            // Aplica filtro na lista atual
            val listaAtualLocal = lerAnuncioViewModel.anuncios.value ?: emptyList()
            aplicarFiltros(listaAtualLocal, categoriaSelecionada)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingNullable = null
    }
}
