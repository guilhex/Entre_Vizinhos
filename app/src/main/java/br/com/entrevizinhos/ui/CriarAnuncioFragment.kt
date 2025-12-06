package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.entrevizinhos.databinding.FragmentCriarAnuncioBinding
import br.com.entrevizinhos.viewmodel.CriarAnuncioViewModel

class CriarAnuncioFragment : Fragment() {

    private var _binding: FragmentCriarAnuncioBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CriarAnuncioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriarAnuncioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializa o ViewModel (Se der erro aqui, crie a classe CriarAnuncioViewModel)
        viewModel = ViewModelProvider(this).get(CriarAnuncioViewModel::class.java)

        // 2. Configura a Toolbar para Voltar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack() // Volta para a tela anterior (Perfil)
        }

        // 3. Configura o botão Publicar
        binding.btnPublicarAnuncio.setOnClickListener {
            val titulo = binding.etTituloAnuncio.text.toString()
            val precoStr = binding.etPrecoAnuncio.text.toString()
            val cidade = binding.etCidadeAnuncio.text.toString()
            val descricao = binding.etDescricaoAnuncio.text.toString()

            if (titulo.isNotEmpty() && precoStr.isNotEmpty()) {
                val preco = precoStr.toDoubleOrNull() ?: 0.0

                // Chama o ViewModel para salvar
                viewModel.publicarAnuncio(titulo, preco, cidade, descricao)

                // Feedback visual
                binding.btnPublicarAnuncio.text = "Publicando..."
                binding.btnPublicarAnuncio.isEnabled = false
            } else {
                Toast.makeText(context, "Preencha pelo menos Título e Preço", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Observa o resultado
        viewModel.resultadoPublicacao.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso) {
                Toast.makeText(context, "Anúncio Publicado!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Volta para o Perfil automaticamente
            } else {
                binding.btnPublicarAnuncio.text = "Tentar Novamente"
                binding.btnPublicarAnuncio.isEnabled = true
                Toast.makeText(context, "Erro ao publicar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}