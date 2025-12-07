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

    // by serve para a criação da val esta de responsa do viewmodel, ele verifica se ja existe na memoria antes de criar
    private val anuncioViewModel: AnuncioViewModel by viewModels()

    private lateinit var adapter: AnuncioAdapter // lateintit permite ser vazio

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
        capturaCategoria()
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
                val action = FeedFragmentDirections.actionFeedFragmentToDetalhesAnuncioFragment(anuncio)
                findNavController().navigate(action)
            }

        binding.rvAnuncios.layoutManager = GridLayoutManager(requireContext(), 2) // coloca em grid 2 colunas
        binding.rvAnuncios.adapter = adapter
    }

    private fun setupObservers() {
        // Mostra a barra de progresso assim que começamos a observar
        binding.pbLoading.visibility = View.VISIBLE

        anuncioViewModel.anuncios.observe(viewLifecycleOwner) { listaAnuncios ->
            // Esconde a barra de progresso
            binding.pbLoading.visibility = View.GONE

            // Atualiza o adaptador

            aplicarFiltros(listaAnuncios, "Todos") // aciona para filtrar a lista de anuncios completa

            //  se a lista estiver vazia
            if (listaAnuncios.isEmpty()) {
                Toast.makeText(requireContext(), "Nenhum anúncio encontrado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun aplicarFiltros(
        listaCompleta: List<Anuncio>,
        categoria: String,
    ) {
        var listaFiltrada =
            if (categoria.equals(getString(R.string.categoria_todos), ignoreCase = true)) {
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
            // Como o ChipGroup está com singleSelection=true, sempre será 0 ou 1 id
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener

            // Acha o Chip dentro do ChipGroup a partir do id selecionado
            val chipSelecionado = group.findViewById<Chip>(checkedId)

            // Pega o texto exibido no chip (ex: "Móveis", "Eletrônicos"...)
            val categoriaSelecionada = chipSelecionado.text.toString()

            // Pega a lista mais recente que o ViewModel tem em memória
            val listaAtual = anuncioViewModel.anuncios.value ?: emptyList()

            // Aplica o filtro com base nessa categoria
            aplicarFiltros(listaAtual, categoriaSelecionada)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
