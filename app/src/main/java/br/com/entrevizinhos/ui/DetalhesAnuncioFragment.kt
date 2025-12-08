package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.entrevizinhos.databinding.FragmentDetalhesAnuncioBinding
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.model.Usuario
import br.com.entrevizinhos.ui.adapter.FotosPagerAdapter
import br.com.entrevizinhos.viewmodel.PerfilViewModel

class DetalhesAnuncioFragment : Fragment() {
    private var bindingNullable: FragmentDetalhesAnuncioBinding? = null
    private val binding get() = bindingNullable!!

    // SafeArgs: Esta linha "apanha" o pacote (Anuncio) que foi enviado pelo Feed
    private val args: DetalhesAnuncioFragmentArgs by navArgs()

    private val perfilViewModel: PerfilViewModel by viewModels()

    private var vendedorAtual: Usuario? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingNullable = FragmentDetalhesAnuncioBinding.inflate(inflater, container, false)
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

        // toolbar
        setupToolbar()

        setupObservers(anuncio)
    }

    private fun setupToolbar() {
        binding.toolbarDetalhes.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setupFotos(fotos: List<String>) {
        if (fotos.isNotEmpty()) {
            binding.vpFotos.adapter = FotosPagerAdapter(fotos)
        }
    }

    private fun setupDados(anuncio: Anuncio) {
        binding.apply {
            tvDetalheTitulo.text = anuncio.titulo
            val precoFormatado = String.format(java.util.Locale.getDefault(), "%.2f", anuncio.preco)
            tvDetalhePreco.text = getString(br.com.entrevizinhos.R.string.preco_format, precoFormatado)
            tvDetalheDescricao.text = anuncio.descricao
            tvDetalheCategoria.text = anuncio.categoria
            tvDetalheLocal.text = anuncio.cidade

            // Se houver fotos, carregamos o carrossel
            if (anuncio.fotos.isNotEmpty()) {
                setupFotos(anuncio.fotos)
            }
        }
    }

    private fun setupListeners() {
        binding.btnWhatsapp.setOnClickListener {
            Toast.makeText(context, "Chat será implementado!", Toast.LENGTH_SHORT).show()
        }

        // Botão de denuncia
        binding.btnDenunciar.setOnClickListener {
            Toast.makeText(context, "Chat será implementado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers(anuncio: Anuncio) {
        perfilViewModel.vendedor.observe(viewLifecycleOwner) { usuarioVendedor ->
            usuarioVendedor?.let { usuario ->
                binding.tvNomeVendedor.text = usuario.nome
                println(usuario.nome)
                binding.tvLocalVendedor.text = usuario.endereco
                vendedorAtual = usuario
            }
        }

        perfilViewModel.carregarVendedor(anuncio.vendedorId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingNullable = null
    }
}
