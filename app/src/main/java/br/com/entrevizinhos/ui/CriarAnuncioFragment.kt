package br.com.entrevizinhos.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.entrevizinhos.databinding.FragmentCriarAnuncioBinding
import br.com.entrevizinhos.viewmodel.CriarAnuncioViewModel

class CriarAnuncioFragment : Fragment() {
    private var _binding: FragmentCriarAnuncioBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CriarAnuncioViewModel
    private var uriFotoSelecionada: Uri? = null

    // Lógica para pegar a foto da galeria
    private val selecionarFoto =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uriFotoSelecionada = uri
                binding.ivFoto1.setImageURI(uri)
                binding.ivFoto1.setPadding(0, 0, 0, 0)
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
        viewModel = ViewModelProvider(this).get(CriarAnuncioViewModel::class.java)

        setupSpinner()
        setupListeners()
        setupObservers()
    }

    private fun setupSpinner() {
        // Opções para o botão clicável de categoria (Spinner)
        val categorias = listOf("Selecione", "Móveis", "Eletrônicos", "Serviços", "Roupas", "Outros")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.spinnerCategoria.adapter = adapter
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.cardFoto1.setOnClickListener {
            selecionarFoto.launch("image/*")
        }

        binding.btnPublicarAnuncio.setOnClickListener {
            // 1. Título
            val titulo = binding.etTituloAnuncio.text.toString()

            // 2. Descrição
            val descricao = binding.etDescricaoAnuncio.text.toString()

            // 3. Preço
            val precoStr = binding.etPrecoAnuncio.text.toString()

            // 4. Categoria
            val categoria = binding.spinnerCategoria.selectedItem.toString()

            // 5. Entrega (Botões selecionáveis - RadioButton)
            val radioSelecionadoId = binding.rgEntrega.checkedRadioButtonId
            val entrega =
                if (radioSelecionadoId != -1) {
                    binding.root
                        .findViewById<RadioButton>(radioSelecionadoId)
                        .text
                        .toString()
                } else {
                    ""
                }

            // 6. Pagamento (Botões selecionáveis - CheckBox)
            val pagamentos = mutableListOf<String>()
            if (binding.cbDinheiro.isChecked) pagamentos.add("Dinheiro")
            if (binding.cbPix.isChecked) pagamentos.add("Pix")
            if (binding.cbCartao.isChecked) pagamentos.add("Cartão")
            val formasPagamento = pagamentos.joinToString(", ")

            // Validação e Envio
            if (titulo.isNotEmpty() && precoStr.isNotEmpty() && categoria != "Selecione") {
                val preco = precoStr.toDoubleOrNull() ?: 0.0

                binding.btnPublicarAnuncio.text = "Publicando..."
                binding.btnPublicarAnuncio.isEnabled = false

                // --- MUDANÇA AQUI: Passando a uriFotoSelecionada ---
                viewModel.publicarAnuncio(
                    titulo = titulo,
                    preco = preco,
                    descricao = descricao,
                    categoria = categoria,
                    entrega = entrega,
                    formasPagamento = formasPagamento,
                    fotoUri = uriFotoSelecionada // <-- Passando a foto escolhida
                )
            } else {
                Toast.makeText(context, "Preencha Título, Preço e Categoria", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.resultadoPublicacao.observe(viewLifecycleOwner) { sucesso ->
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
