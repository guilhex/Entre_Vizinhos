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
import br.com.entrevizinhos.viewmodel.AnuncioViewModel
import com.google.android.material.chip.Chip

class FeedFragment : Fragment() {
    private var bindingNullable: FragmentFeedBinding? = null

    private val binding get() = bindingNullable!!

    // usa ViewModel compartilhado entre fragments
    private val anuncioViewModel: AnuncioViewModel by activityViewModels()

    private lateinit var adapter: AnuncioAdapter // lateintit permite ser vazio

    // Guarda a lista e a categoria atuais para reaplicar filtros quando favoritos mudarem
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

        // define categoria inicial
        categoriaAtual = getString(R.string.categoria_todos)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter =
            AnuncioAdapter(
                listaAnuncios = emptyList(),
                favoritosIds = anuncioViewModel.favoritosIds.value ?: emptySet(),
                onAnuncioClick = { anuncio ->
                    val action =
                        FeedFragmentDirections.actionFeedFragmentToDetalhesAnuncioFragment(anuncio)
                    findNavController().navigate(action)
                },
                onFavoritoClick = { anuncio ->
                    // delega pro ViewModel alterar favorito do usuário logado
                    anuncioViewModel.onFavoritoClick(anuncio.id)
                },
            )

        binding.rvAnuncios.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAnuncios.adapter = adapter
    }

    private fun setupObservers() {
        // Mostra a barra de progresso assim que começamos a observar
        binding.pbLoading.visibility = View.VISIBLE

        anuncioViewModel.anuncios.observe(viewLifecycleOwner) { listaAnuncios ->
            // Esconde a barra de progresso
            binding.pbLoading.visibility = View.GONE

            // guarda lista atual e reaplica filtro
            listaAtual = listaAnuncios
            aplicarFiltros(listaAnuncios, categoriaAtual.ifEmpty { getString(R.string.categoria_todos) })

            //  se a lista estiver vazia
            if (listaAnuncios.isEmpty()) {
                Toast.makeText(requireContext(), "Nenhum anúncio encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        // Reaplica filtros quando favoritos mudarem
        anuncioViewModel.favoritosIds.observe(viewLifecycleOwner) { _ ->
            aplicarFiltros(listaAtual, categoriaAtual.ifEmpty { getString(R.string.categoria_todos) })
        }
    }

    private fun aplicarFiltros(
        listaCompleta: List<Anuncio>,
        categoria: String,
    ) {
        val labelTodos = getString(R.string.categoria_todos)
        val listaFiltrada =
            if (categoria.equals(labelTodos, ignoreCase = true)) {
                listaCompleta
            } else {
                listaCompleta.filter { anuncio ->
                    anuncio.categoria.equals(categoria, ignoreCase = true)
                }
            }

        adapter.atualizarLista(listaFiltrada, anuncioViewModel.favoritosIds.value ?: emptySet())
    }

    private fun capturaCategoria() {
        binding.chipGroupCategorias.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener // se vier vazio cancela operação

            // Acha o Chip dentro do ChipGroup a partir do id selecionado
            val chipSelecionado = group.findViewById<Chip>(checkedId)

            // Pega o texto exibido no chip (ex: "Móveis", "Eletrônicos"...)
            val categoriaSelecionada = chipSelecionado.text.toString()

            // atualiza a categoria atual
            categoriaAtual = categoriaSelecionada

            // Pega a lista mais recente que o ViewModel tem em memória
            val listaAtualLocal = anuncioViewModel.anuncios.value ?: emptyList()

            // Aplica o filtro com base nessa categoria
            aplicarFiltros(listaAtualLocal, categoriaSelecionada)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingNullable = null
    }
}
