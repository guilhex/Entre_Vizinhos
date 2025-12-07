package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.entrevizinhos.R
import br.com.entrevizinhos.databinding.FragmentDetalhesAnuncioBinding
import br.com.entrevizinhos.model.Anuncio
import com.bumptech.glide.Glide

class DetalhesAnuncioFragment : Fragment() {
    private var _binding: FragmentDetalhesAnuncioBinding? = null
    private val binding get() = _binding!!

    // SafeArgs: Esta linha "apanha" o pacote (Anuncio) que foi enviado pelo Feed
    private val args: DetalhesAnuncioFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetalhesAnuncioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Recuperamos o anúncio que chegou
        val anuncio = args.anuncio

        // 2. Preenchemos a tela
        setupDados(anuncio)

        // 3. Configuramos os botões
        setupListeners()
    }

    private fun setupDados(anuncio: Anuncio) {
        binding.apply {
            tvDetalheTitulo.text = anuncio.titulo
            tvDetalhePreco.text = "R$ ${String.format("%.2f", anuncio.preco)}"
            tvDetalheDescricao.text = anuncio.descricao
            tvDetalheLocal.text = anuncio.cidade

            // Se houver fotos, carregamos a primeira com o Glide
            if (anuncio.fotos.isNotEmpty()) {
                Glide
                    .with(root.context)
                    .load(anuncio.fotos[0])
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivDetalheFoto)
            }
        }
    }

    private fun setupListeners() {
        // Botão de Voltar (Seta na Toolbar)
        binding.toolbarDetalhes.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // Botão de Contacto
        binding.btnContato.setOnClickListener {
            Toast.makeText(context, "Chat será implementado na Fase 3!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
