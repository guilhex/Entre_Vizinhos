package br.com.entrevizinhos.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.entrevizinhos.databinding.FragmentEditarPerfilBinding
import br.com.entrevizinhos.model.Usuario
import br.com.entrevizinhos.viewmodel.PerfilViewModel
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import android.graphics.BitmapFactory
import android.util.Base64

class EditarPerfilFragment : Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PerfilViewModel by viewModels()
    private val args: EditarPerfilFragmentArgs by navArgs()

    private var novaFotoUri: Uri? = null

    // Seletor de foto da galeria
    private val selecionarFoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            novaFotoUri = uri
            binding.ivFotoEdit.setImageURI(uri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuarioAtual = args.usuario
        preencherCampos(usuarioAtual)
        setupListeners(usuarioAtual)
    }

    private fun preencherCampos(usuario: Usuario) {
        binding.etNome.setText(usuario.nome)
        binding.etEndereco.setText(usuario.endereco)
        binding.etTelefone.setText(usuario.telefone)
        binding.etCnpj.setText(usuario.cnpj)

        // Carrega foto (URL ou Base64)
        if (usuario.fotoUrl.isNotEmpty()) {
            if (usuario.fotoUrl.startsWith("data:image")) {
                try {
                    val base64Clean = usuario.fotoUrl.substringAfter(",")
                    val decodedBytes = Base64.decode(base64Clean, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    binding.ivFotoEdit.setImageBitmap(bitmap)
                } catch (e: Exception) { }
            } else {
                Glide.with(this).load(usuario.fotoUrl).into(binding.ivFotoEdit)
            }
        }
    }

    private fun setupListeners(usuario: Usuario) {
        binding.toolbarEditar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.btnCancelar.setOnClickListener { findNavController().popBackStack() }

        binding.cardFotoEdit.setOnClickListener { selecionarFoto.launch("image/*") }

        binding.btnSalvar.setOnClickListener {
            val novoNome = binding.etNome.text.toString()
            val novoEndereco = binding.etEndereco.text.toString()
            val novoTelefone = binding.etTelefone.text.toString()
            val novoCnpj = binding.etCnpj.text.toString()

            if (novoNome.isNotEmpty()) {
                val usuarioAtualizado = usuario.copy(
                    nome = novoNome,
                    endereco = novoEndereco,
                    telefone = novoTelefone,
                    cnpj = novoCnpj
                )

                // Se houver nova foto, o ViewModel deve tratar (precisa implementar lógica de upload no VM se quiser)
                // Por simplicidade, salvamos os textos primeiro
                viewModel.salvarPerfil(usuarioAtualizado)
                Toast.makeText(context, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Nome é obrigatório", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}