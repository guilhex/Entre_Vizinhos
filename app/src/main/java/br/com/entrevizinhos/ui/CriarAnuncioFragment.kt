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

    private var uriFoto1: Uri? = null
    private var uriFoto2: Uri? = null
    private var uriFoto3: Uri? = null

    private var slotFotoSelecionado = 1

    private val selecionarFoto =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
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
        viewModel = ViewModelProvider(this).get(CriarAnuncioViewModel::class.java)

        setupToolbar()
        setupSpinner()
        setupListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupSpinner() {
        val categorias = listOf("Selecione", "Móveis", "Eletrônicos", "Serviços", "Roupas", "Outros")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.spinnerCategoria.adapter = adapter
    }

    private fun setupListeners() {
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

        binding.btnPublicarAnuncio.setOnClickListener {
            val titulo = binding.etTituloAnuncio.text.toString().trim()
            val descricao = binding.etDescricaoAnuncio.text.toString().trim()
            val precoStr = binding.etPrecoAnuncio.text.toString().trim()
            val categoria = binding.spinnerCategoria.selectedItem.toString()

            val radioSelecionadoId = binding.rgEntrega.checkedRadioButtonId
            val entrega = if (radioSelecionadoId != -1) {
                binding.root.findViewById<RadioButton>(radioSelecionadoId).text.toString()
            } else {
                ""
            }

            val pagamentos = mutableListOf<String>()
            if (binding.cbDinheiro.isChecked) pagamentos.add("Dinheiro")
            if (binding.cbPix.isChecked) pagamentos.add("Pix")
            if (binding.cbCartao.isChecked) pagamentos.add("Cartão")
            val formasPagamento = pagamentos.joinToString(", ")

            if (titulo.isNotEmpty() && precoStr.isNotEmpty() && categoria != "Selecione") {
                val preco = precoStr.toDoubleOrNull() ?: 0.0

                val fotos = listOfNotNull(uriFoto1, uriFoto2, uriFoto3)

                binding.btnPublicarAnuncio.text = "Publicando..."
                binding.btnPublicarAnuncio.isEnabled = false

                viewModel.publicarAnuncio(
                    titulo = titulo,
                    preco = preco,
                    descricao = descricao,
                    categoria = categoria,
                    entrega = entrega,
                    formasPagamento = formasPagamento,
                    fotos = fotos,
                    context = requireContext(),
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