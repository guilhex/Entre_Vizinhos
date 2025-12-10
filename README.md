# 🏘️ Entre Vizinhos

> O Marketplace local de Urutaí-GO para conectar vizinhos, compradores e vendedores.

![Badge Android](https://img.shields.io/badge/Android-Kotlin-3DDC84?style=flat&logo=android&logoColor=white)
![Badge Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=flat&logo=firebase&logoColor=black)
![Badge Status](https://img.shields.io/badge/Status-Concluído-success)

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
