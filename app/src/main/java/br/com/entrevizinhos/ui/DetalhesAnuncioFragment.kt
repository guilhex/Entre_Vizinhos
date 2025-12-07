package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.entrevizinhos.databinding.FragmentDetalhesAnuncioBinding
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.ui.adapter.FotosPagerAdapter

class DetalhesAnuncioFragment : Fragment() {
    private var _binding: FragmentDetalhesAnuncioBinding? = null
    private val binding get() = _binding!!
    private val args: DetalhesAnuncioFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetalhesAnuncioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val anuncio = args.anuncio
        setupToolbar()
        setupFotos(anuncio.fotos)
        setupDados(anuncio)
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setupFotos(fotos: List<String>) {
        if (fotos.isNotEmpty()) {
            binding.vpFotos.adapter = FotosPagerAdapter(fotos)
        }
    }

    private fun setupDados(anuncio: Anuncio) {
        binding.tvDetalheTitulo.text = anuncio.titulo
        binding.tvDetalhePreco.text = "R$ ${String.format("%.2f", anuncio.preco)}"
        binding.tvDetalheDescricao.text = anuncio.descricao
        binding.tvDetalheLocal.text = anuncio.cidade
    }

    private fun setupListeners() {
        binding.btnContato.setOnClickListener {
            Toast.makeText(context, "Chat será implementado na Fase 3!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}