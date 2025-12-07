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
import android.graphics.Matrix
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream

class EditarPerfilFragment : Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PerfilViewModel by viewModels()
    private val args: EditarPerfilFragmentArgs by navArgs()

    private var novaFotoUri: Uri? = null

    private val selecionarFoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            novaFotoUri = uri
            binding.ivFotoEdit.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuarioAtual = args.usuario
        setupToolbar()
        preencherCampos(usuarioAtual)
        setupListeners(usuarioAtual)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun preencherCampos(usuario: Usuario) {
        binding.apply {
            etNome.setText(usuario.nome)
            etEndereco.setText(usuario.endereco)
            etTelefone.setText(usuario.telefone)
            etEmail.setText(usuario.email)
            etCnpj.setText(usuario.cnpj)
        }

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
        binding.cardFotoEdit.setOnClickListener {
            selecionarFoto.launch("image/*")
        }

        binding.btnSalvar.setOnClickListener {
            val novoNome = binding.etNome.text.toString()
            val novoEndereco = binding.etEndereco.text.toString()
            val novoTelefone = binding.etTelefone.text.toString()
            val novoCnpj = binding.etCnpj.text.toString()

            if (novoNome.isNotEmpty()) {
                var usuarioAtualizado = usuario.copy(
                    nome = novoNome,
                    endereco = novoEndereco,
                    telefone = novoTelefone,
                    cnpj = novoCnpj
                )

                // Se selecionou nova foto, converte para Base64 com rotação correta
                if (novaFotoUri != null) {
                    val inputStream: InputStream? = requireContext().contentResolver.openInputStream(novaFotoUri!!)
                    if (inputStream != null) {
                        // Lê a imagem original
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()

                        // Obtém a rotação EXIF
                        val inputStream2: InputStream? = requireContext().contentResolver.openInputStream(novaFotoUri!!)
                        if (inputStream2 != null) {
                            val exif = ExifInterface(inputStream2)
                            val orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                            )
                            inputStream2.close()

                            // Rotaciona a imagem se necessário
                            bitmap = rotacionarBitmap(bitmap, orientation)
                        }

                        // Converte para Base64
                        val outputStream = java.io.ByteArrayOutputStream()
                        bitmap?.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
                        val bytes = outputStream.toByteArray()

                        val mimeType = requireContext().contentResolver.getType(novaFotoUri!!) ?: "image/jpeg"
                        val base64Foto = "data:$mimeType;base64,${Base64.encodeToString(bytes, Base64.DEFAULT)}"
                        usuarioAtualizado = usuarioAtualizado.copy(fotoUrl = base64Foto)
                    }
                }

                viewModel.salvarPerfil(usuarioAtualizado)
                Toast.makeText(context, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Nome é obrigatório", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun rotacionarBitmap(bitmap: android.graphics.Bitmap?, orientation: Int): android.graphics.Bitmap? {
        if (bitmap == null) return null

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }

        return android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}