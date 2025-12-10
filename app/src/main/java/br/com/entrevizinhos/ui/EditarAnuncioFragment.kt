package br.com.entrevizinhos.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.entrevizinhos.databinding.FragmentCriarAnuncioBinding
import br.com.entrevizinhos.viewmodel.CriarAnuncioViewModel
import com.google.android.material.chip.Chip

/**
 * Fragment para edição de anúncios existentes
 * Reutiliza layout do CriarAnuncioFragment com dados pré-preenchidos
 */
class EditarAnuncioFragment : Fragment() {

    private var _binding: FragmentCriarAnuncioBinding? = null // Reutiliza layout de criação
    private val binding get() = _binding!! // Acesso seguro ao binding
    private lateinit var viewModel: CriarAnuncioViewModel // ViewModel compartilhado

    private val args: EditarAnuncioFragmentArgs by navArgs() // Anúncio a ser editado

    // Variáveis de estado para seleções do usuário
    private var entregaSelecionada = "" // Modalidade de entrega selecionada
    private val pagamentosSelecionados = mutableSetOf<String>() // Formas de pagamento

    // Variáveis para gerenciar novas fotos (se o usuário trocar)
    private var uriFoto1: Uri? = null
    private var uriFoto2: Uri? = null
    private var uriFoto3: Uri? = null
    private var slotFotoSelecionado = 1 // Controla qual slot está sendo editado

    // Launcher para seleção de novas fotos (substituição das existentes)
    private val selecionarFoto =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // Atualiza slot correspondente com nova foto
                when (slotFotoSelecionado) {
                    1 -> {
                        uriFoto1 = uri // Armazena URI da nova foto
                        binding.ivFoto1.setImageURI(uri) // Mostra preview
                        binding.ivFoto1.setPadding(0, 0, 0, 0) // Remove padding padrão
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

        // Inicializa ViewModel (mesmo usado para criar e editar)
        viewModel = ViewModelProvider(this).get(CriarAnuncioViewModel::class.java)

        val anuncio = args.anuncio // Anúncio recebido para edição

        // Personalização da tela para modo edição
        binding.toolbar.title = "Editar Anúncio" // Muda título da toolbar
        // Preenche campos com dados existentes
        binding.etTituloAnuncio.setText(anuncio.titulo)
        binding.etDescricaoAnuncio.setText(anuncio.descricao)
        binding.etPrecoAnuncio.setText(anuncio.preco.toString())

        // Configuração da tela com dados existentes
        setupToolbar() // Botão voltar
        setupSpinner(anuncio.categoria) // Categoria pré-selecionada
        setupChipsEntrega(anuncio.entrega) // Entrega pré-selecionada
        setupChipsPagamento(anuncio.formasPagamento) // Pagamentos pré-selecionados
        setupFotos(anuncio.fotos) // Carrega fotos existentes
        setupFotoListeners() // Listeners para trocar fotos
        setupListeners(anuncio) // Botão atualizar
        setupObservers() // Observa resultado da operação
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupSpinner(categoriaAtual: String) {
        val categorias = listOf("Selecione", "Móveis", "Eletrônicos", "Serviços", "Roupas", "Outros")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.spinnerCategoria.adapter = adapter

        val posicao = categorias.indexOf(categoriaAtual)
        if (posicao >= 0) {
            binding.spinnerCategoria.setSelection(posicao)
        }
    }

    private fun setupChipsEntrega(entregaAtual: String) {
        binding.chipGroupEntrega.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = binding.root.findViewById<Chip>(checkedIds[0])
                entregaSelecionada = chip.text.toString()
            } else {
                entregaSelecionada = ""
            }
        }

        when (entregaAtual) {
            "Somente retirada" -> binding.chipRetirada.isChecked = true
            "Entrega disponível" -> binding.chipEntrega.isChecked = true
            "Retirada e entrega" -> binding.chipRetiradaEntrega.isChecked = true
        }
    }

    private fun setupChipsPagamento(formasPagamentoStr: String) {
        binding.chipGroupPagamento.setOnCheckedStateChangeListener { group, checkedIds ->
            pagamentosSelecionados.clear()

            for (id in checkedIds) {
                val chip = binding.root.findViewById<Chip>(id)
                if (chip != null) {
                    pagamentosSelecionados.add(chip.text.toString())
                }
            }
        }

        val formasPagamento = formasPagamentoStr.split(",").map { it.trim() }

        for (forma in formasPagamento) {
            when (forma) {
                "Dinheiro" -> binding.chipDinheiro.isChecked = true
                "PIX" -> binding.chipPix.isChecked = true
                "Cartão Débito" -> binding.chipCartaoDebito.isChecked = true
                "Cartão Crédito" -> binding.chipCartaoCredito.isChecked = true
            }
        }
    }

    // ===== CARREGAR FOTOS EXISTENTES =====
    private fun setupFotos(fotos: List<String>) {
        if (fotos.isNotEmpty()) {
            for ((index, fotoString) in fotos.withIndex()) {
                if (index >= 3) break // Máximo 3 fotos

                if (fotoString.startsWith("data:image")) {
                    try {
                        val base64Clean = fotoString.substringAfter(",")
                        val decodedBytes = Base64.decode(base64Clean, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        when (index) {
                            0 -> {
                                binding.ivFoto1.setImageBitmap(bitmap)
                                binding.ivFoto1.setPadding(0, 0, 0, 0)
                            }
                            1 -> {
                                binding.ivFoto2.setImageBitmap(bitmap)
                                binding.ivFoto2.setPadding(0, 0, 0, 0)
                            }
                            2 -> {
                                binding.ivFoto3.setImageBitmap(bitmap)
                                binding.ivFoto3.setPadding(0, 0, 0, 0)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun setupFotoListeners() {
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
    }

    private fun setupListeners(anuncio: br.com.entrevizinhos.model.Anuncio) {
        binding.btnPublicarAnuncio.text = "Atualizar Anúncio"
        binding.btnPublicarAnuncio.setOnClickListener {
            atualizarAnuncio(anuncio)
        }
    }

    private fun atualizarAnuncio(anuncio: br.com.entrevizinhos.model.Anuncio) {
        val titulo = binding.etTituloAnuncio.text.toString().trim()
        val descricao = binding.etDescricaoAnuncio.text.toString().trim()
        val precoStr = binding.etPrecoAnuncio.text.toString().trim()
        val categoria = binding.spinnerCategoria.selectedItem.toString()

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

        val preco = precoStr.toDoubleOrNull() ?: 0.0
        val formasPagamento = pagamentosSelecionados.joinToString(", ")

        binding.btnPublicarAnuncio.text = "Atualizando..."
        binding.btnPublicarAnuncio.isEnabled = false

        viewModel.atualizarAnuncio(
            anuncioId = anuncio.id,
            titulo = titulo,
            preco = preco,
            descricao = descricao,
            categoria = categoria,
            entrega = entregaSelecionada,
            formasPagamento = formasPagamento,
            context = requireContext(),
        )
    }

    private fun setupObservers() {
        viewModel.resultadoPublicacao.observe(viewLifecycleOwner) { sucesso ->
            binding.btnPublicarAnuncio.isEnabled = true
            binding.btnPublicarAnuncio.text = "Atualizar Anúncio"

            if (sucesso) {
                Toast.makeText(context, "Anúncio Atualizado com Sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Erro ao atualizar. Verifique sua conexão.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}