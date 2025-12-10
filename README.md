# 🏘️ Entre Vizinhos

> O Marketplace local de Urutaí-GO para conectar vizinhos, compradores e vendedores.

![Badge Android](https://img.shields.io/badge/Android-Kotlin-3DDC84?style=flat&logo=android&logoColor=white)
![Badge Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=flat&logo=firebase&logoColor=black)
![Badge Status](https://img.shields.io/badge/Status-Em_Desenvolvimento-yellow)

## 📄 Sobre o Projeto

O **Entre Vizinhos** é um aplicativo nativo Android desenvolvido como Projeto Prático da Disciplina (PPD). O objetivo é facilitar a divulgação e descoberta de produtos e serviços locais na cidade de Urutaí-GO, centralizando ofertas que antes ficavam dispersas em grupos de mensagens desorganizados.

O app permite que usuários anunciem produtos, filtrem por categorias e entrem em contato direto com vendedores.

---

## 📱 Funcionalidades

- **🔐 Autenticação Segura:** Login exclusivo via Conta Google (Firebase Authentication).
- **🏠 Feed de Anúncios:** Visualização de produtos em grade (`RecyclerView`) com carregamento dinâmico.
- **🔍 Filtros por Categoria:** Navegação rápida entre Móveis, Eletrônicos, Ferramentas, etc.
- **👤 Gestão de Perfil:** Edição de dados cadastrais (Telefone, Endereço) e foto de perfil.
- **📢 Publicação de Anúncios:** Formulário completo para criar novos anúncios com fotos.
- **❤️ Minha Coleção:** Área para o usuário gerenciar (editar/excluir) seus próprios anúncios.
- **💾 Offline First (Parcial):** Estratégia de persistência de imagens via Base64 no Firestore.

---

## 🛠️ Tecnologias Utilizadas

O projeto foi construído utilizando as melhores práticas de desenvolvimento Android moderno:

- **Linguagem:** [Kotlin](https://kotlinlang.org/)
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **Interface:** XML com ViewBinding
- **Navegação:** Jetpack Navigation Component (Single Activity Architecture)
- **Banco de Dados:** Cloud Firestore (NoSQL)
- **Autenticação:** Firebase Auth
- **Carregamento de Imagens:** [Glide](https://github.com/bumptech/glide) & Decodificação Base64 Nativa
- **Assincronismo:** Coroutines

---

## 📸 Telas do Projeto

| Login | Feed Principal | Detalhes do Anúncio |
|:---:|:---:|:---:|
| <img src="screenshots/login.png" width="200"> | <img src="screenshots/feed.png" width="200"> | <img src="screenshots/detalhes.png" width="200"> |

| Perfil | Criar Anúncio | Meus Favoritos |
|:---:|:---:|:---:|
| <img src="screenshots/perfil.png" width="200"> | <img src="screenshots/criar_anuncio.png" width="200"> | <img src="screenshots/favoritos.png" width="200"> |

> *Nota: As imagens acima são ilustrativas. Para ver o app em funcionamento, clone o repositório.*

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Android Studio Iguana ou superior.
- JDK 17 ou superior.
- Uma conta no Firebase.

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/guilhex/Entre_Vizinhos.git](https://github.com/guilhex/Entre_Vizinhos.git)

Configuração do Firebase:

Crie um projeto no Firebase Console.

Adicione um app Android com o pacote br.com.entrevizinhos.

Baixe o arquivo google-services.json.

Importante: Coloque o arquivo google-services.json dentro da pasta app/ do projeto.

No console do Firebase, habilite o Authentication (Google Sign-In) e o Firestore Database.

Execute o App:

Abra o projeto no Android Studio.

Aguarde o Gradle sincronizar.

Conecte um dispositivo físico ou emulador.

Clique no botão Run (▶️).

⚠️ Observações Técnicas
Armazenamento de Imagens
Devido a restrições de custos em planos de servidor para fins acadêmicos, este projeto utiliza uma abordagem alternativa para armazenamento de imagens:

As imagens são convertidas para Base64 e salvas diretamente como Strings dentro dos documentos do Firestore.

O app possui um decodificador próprio (BitmapFactory) para transformar essas Strings de volta em Bitmaps e corrigir a rotação (EXIF) de fotos tiradas pela câmera.

Trabalhos Futuros
[ ] Migração para Firebase Storage.

[ ] Implementação de Chat interno ou link direto para WhatsApp.

[ ] Moderação automática de conteúdo.

👨‍💻 Autores
<table> <tr> <td align="center"> <a href="https://www.google.com/search?q=https://github.com/SEU_USUARIO_AQUI"> <img src="https://www.google.com/search?q=https://avatars.githubusercontent.com/u/SEU_ID_AQUI%3Fv%3D4" width="100px;" alt="Foto do Artur"/>


<sub><b>Artur Duarte Monteiro</b></sub> </a> </td> <td align="center"> <a href="https://www.google.com/search?q=https://github.com/guilhex"> <img src="https://www.google.com/search?q=https://avatars.githubusercontent.com/guilhex" width="100px;" alt="Foto do Guilherme"/>


<sub><b>Guilherme Pereira da Silva</b></sub> </a> </td> </tr> </table>
