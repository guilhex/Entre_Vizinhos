package br.com.entrevizinhos.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.entrevizinhos.databinding.FragmentCriarAnuncioBinding
import br.com.entrevizinhos.viewmodel.CriarAnuncioViewModel
import com.google.android.material.chip.Chip

/**
 * Fragment responsável por criar novos anúncios
 */
class CriarAnuncioFragment : Fragment() {

    private var _binding: FragmentCriarAnuncioBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CriarAnuncioViewModel

    // Variáveis para gerenciar fotos
    private var uriFoto1: Uri? = null
    private var uriFoto2: Uri? = null
    private var uriFoto3: Uri? = null
    private var slotFotoSelecionado = 1

    // Variáveis para seleções do usuário
    private var entregaSelecionada = ""
    private val pagamentosSelecionados = mutableSetOf<String>()

    // Launcher para seleção de fotos da galeria
    private val selecionarFoto =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // Armazena foto no slot correto e mostra preview
                when (slotFotoSelecionado) {
                    1 -> {
                        uriFoto1 = uri
                        binding.ivFoto1.setImageURI(uri)
                        binding.ivFoto1.setPadding(0, 0, 0, 0)
                    }
                    2 -> {
                        uriFoto2 = uri
                        binding.ivFoto2.setImageURI(uri)
                        binding.ivFoto2.setPadding(0, 0, 0, 0)
                    }
                    3 -> {
                        uriFoto3 = uri
                        binding.ivFoto3.setImageURI(uri)
                        binding.ivFoto3.setPadding(0, 0, 0, 0)
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCriarAnuncioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa ViewModel
        viewModel = ViewModelProvider(this).get(CriarAnuncioViewModel::class.java)

        // Configura componentes da tela
        setupToolbar()
        setupSpinner()
        setupListeners()
        setupObservers()
    }

    // Configura toolbar com botão voltar
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Configura spinner de categorias
    private fun setupSpinner() {
        val categorias = listOf("Selecione", "Móveis", "Eletrônicos", "Serviços", "Roupas", "Outros")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.spinnerCategoria.adapter = adapter
    }

    // Configura listeners dos elementos da tela
    private fun setupListeners() {
        // Listeners para seleção de fotos
        binding.cardFoto1.setOnClickListener {
            slotFotoSelecionado = 1
            selecionarFoto.launch("image/*")
        }
        binding.cardFoto2.setOnClickListener {
            slotFotoSelecionado = 2
            selecionarFoto.launch("image/*")
        }
        binding.cardFoto3.setOnClickListener {
            slotFotoSelecionado = 3
            selecionarFoto.launch("image/*")
        }

        // Listener para chips de entrega (seleção única)
        binding.chipGroupEntrega.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = binding.root.findViewById<Chip>(checkedIds[0])
                entregaSelecionada = chip.text.toString()
            } else {
                entregaSelecionada = ""
            }
        }

        // Listener para chips de pagamento (seleção múltipla)
        binding.chipGroupPagamento.setOnCheckedStateChangeListener { group, checkedIds ->
            pagamentosSelecionados.clear()
            for (id in checkedIds) {
                val chip = binding.root.findViewById<Chip>(id)
                if (chip != null) {
                    pagamentosSelecionados.add(chip.text.toString())
                }
            }
        }

        // Listener do botão publicar
        binding.btnPublicarAnuncio.setOnClickListener {
            publicarAnuncio()
        }
    }

    // Valida campos e publica anúncio
    private fun publicarAnuncio() {
        // Coleta dados dos campos
        val titulo = binding.etTituloAnuncio.text.toString().trim()
        val descricao = binding.etDescricaoAnuncio.text.toString().trim()
        val precoStr = binding.etPrecoAnuncio.text.toString().trim()
        val categoria = binding.spinnerCategoria.selectedItem.toString()

        // Validações obrigatórias
        if (titulo.isEmpty() || precoStr.isEmpty() || categoria == "Selecione") {
            Toast.makeText(context, "Preencha Título, Preço e Categoria", Toast.LENGTH_SHORT).show()
            return
        }

        if (entregaSelecionada.isEmpty()) {
            Toast.makeText(context, "Selecione uma opção de Entrega/Retirada", Toast.LENGTH_SHORT).show()
            return
        }

        if (pagamentosSelecionados.isEmpty()) {
            Toast.makeText(context, "Selecione pelo menos uma forma de pagamento", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepara dados para envio
        val preco = precoStr.toDoubleOrNull() ?: 0.0
        val fotos = listOfNotNull(uriFoto1, uriFoto2, uriFoto3)
        val formasPagamento = pagamentosSelecionados.joinToString(", ")

        // Feedback visual durante publicação
        binding.btnPublicarAnuncio.text = "Publicando..."
        binding.btnPublicarAnuncio.isEnabled = false

        // Envia para ViewModel
        viewModel.publicarAnuncio(
            titulo = titulo,
            preco = preco,
            descricao = descricao,
            categoria = categoria,
            entrega = entregaSelecionada,
            formasPagamento = formasPagamento,
            fotos = fotos,
            context = requireContext(),
        )
    }

    // Observa resultado da publicação
    private fun setupObservers() {
        viewModel.resultadoPublicacao.observe(viewLifecycleOwner) { sucesso ->
            // Restaura estado do botão
            binding.btnPublicarAnuncio.isEnabled = true
            binding.btnPublicarAnuncio.text = "Publicar Agora"

            if (sucesso) {
                Toast.makeText(context, "Anúncio Publicado com Sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Erro ao publicar. Verifique sua conexão.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}