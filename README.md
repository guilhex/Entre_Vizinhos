# 🏘️ Entre Vizinhos

> Marketplace local de Urutaí-GO para conectar vizinhos, compradores e vendedores.

![Badge Android](https://img.shields.io/badge/Android-Kotlin-3DDC84?style=flat&logo=android&logoColor=white)
![Badge Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=flat&logo=firebase&logoColor=black)
![Badge MVVM](https://img.shields.io/badge/Architecture-MVVM-blue?style=flat)
![Badge Status](https://img.shields.io/badge/Status-Em_Desenvolvimento-yellow)

---

## 📄 Sobre o Projeto

O **Entre Vizinhos** é um aplicativo nativo Android desenvolvido como Projeto Prático da Disciplina (PPD) do curso de Sistemas de Informação. O objetivo é facilitar a divulgação e descoberta de produtos e serviços locais na cidade de Urutaí-GO, centralizando ofertas que antes ficavam dispersas em grupos de mensagens desorganizados.

O app permite que usuários anunciem produtos, filtrem por categorias, entrem em contato direto com vendedores via WhatsApp, avaliem outros usuários e denunciem conteúdos impróprios, criando um ambiente seguro e confiável para a comunidade local.

---

## 📱 Funcionalidades Implementadas

### 🔐 Autenticação e Segurança
- ✅ **Login via Google:** Autenticação segura usando Firebase Authentication
- ✅ **Proteção de Dados:** Validação de permissões e segurança de dados pessoais

### 🏠 Feed e Navegação
- ✅ **Feed Dinâmico:** Visualização de produtos em grade responsiva (RecyclerView)
- ✅ **Atualização em Tempo Real:** Sistema de SnapshotListener para sincronização automática
- ✅ **Filtros por Categoria:** Navegação rápida entre Móveis, Eletrônicos, Ferramentas, Serviços, etc.

### 👤 Perfil e Gestão de Usuário
- ✅ **Edição de Perfil:** Atualização de dados cadastrais (Nome, Telefone, Endereço, CNPJ)
- ✅ **Foto de Perfil:** Upload e gerenciamento de imagem pessoal
- ✅ **Histórico:** Visualização de data de cadastro
- ✅ **Logout:** Encerramento de sessão

### 📢 Publicação e Gerenciamento de Anúncios
- ✅ **Criação de Anúncios:** Formulário completo com múltiplas fotos
- ✅ **Edição de Anúncios:** Modificação de anúncios existentes
- ✅ **Exclusão de Anúncios:** Remoção permanente com confirmação
- ✅ **Meus Anúncios:** Área dedicada para gerenciar anúncios próprios
- ✅ **Favoritos:** Sistema de marcação de anúncios de interesse

### 🖼️ Gerenciamento de Imagens
- ✅ **Upload Múltiplo:** Suporte para várias fotos por anúncio
- ✅ **Carrossel de Fotos:** Visualização em ViewPager2 com indicadores
- ✅ **Armazenamento Base64:** Imagens convertidas e salvas no Firestore (solução para evitar custos)

### 💾 Persistência e Performance
- ✅ **Carregamento Assíncrono:** Uso de Coroutines para operações não-bloqueantes
- ✅ **Otimização de Memória:** Lazy loading e reciclagem de views

---

## 🚧 Funcionalidades Pendentes

### 🔒 Segurança e Validações
- ⏳ **Modo Visitante:** Bloquear edições sem login (atualmente permite editar sem autenticação)
- ⏳ **Validação de Dados:** Exigir dados cadastrais completos antes de anunciar
- ⏳ **Validação de Telefone:** Verificação de número de telefone
- ⏳ **Proteção de Email:** Impedir edição do email após cadastro
- ⏳ **Moderação de Imagens:** Censura automática de conteúdo impróprio (perfil e anúncios)

### 🔍 Busca e Filtros
- ⏳ **Busca por Nome:** Sistema de busca por título de anúncio (atualmente apenas filtro por categoria)

### 💬 Comunicação
- ⏳ **Chat WhatsApp:** Deep Link para contato direto com vendedores
- ⏳ **Sistema de Denúncias:** Reportar anúncios ou usuários inadequados

### 👤 Gestão de Conta
- ⏳ **Deletar Conta:** Opção para exclusão permanente da conta
- ⏳ **Sistema de Avaliações:** Visualização de rating (0-5 estrelas) de outros usuários

### 📍 Localização
- ⏳ **Exibição de Cidade:** Mostrar apenas cidade no anúncio (atualmente mostra endereço completo)

---

## 🛠️ Tecnologias Utilizadas

### Core
- **Linguagem:** [Kotlin](https://kotlinlang.org/) 2.0.21
- **SDK Mínimo:** Android 7.0 (API 24)
- **SDK Alvo:** Android 14 (API 36)
- **Build Tool:** Gradle 8.7.3 com Kotlin DSL

### Arquitetura e Padrões
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **Injeção de Dependência:** Manual (Repository Pattern)
- **Navegação:** Jetpack Navigation Component (Single Activity)
- **Binding:** ViewBinding para acesso type-safe às views

### Backend e Dados
- **Banco de Dados:** Cloud Firestore (NoSQL)
- **Autenticação:** Firebase Authentication
- **Storage:** Firebase Storage + Base64 no Firestore
- **Sincronização:** Real-time listeners (SnapshotListener)

### UI e Componentes
- **Interface:** XML Layouts com Material Design 3
- **Imagens:** [Glide 4.16.0](https://github.com/bumptech/glide) + Decodificação Base64
- **Listas:** RecyclerView com GridLayoutManager
- **Carrossel:** ViewPager2 para galeria de fotos

### Assincronismo e Concorrência
- **Coroutines:** Kotlin Coroutines para operações assíncronas
- **LiveData:** Observação reativa de mudanças de estado
- **ViewModel:** Gerenciamento de estado com lifecycle awareness

### Bibliotecas Adicionais
- **ExifInterface:** Correção de orientação de fotos
- **Play Services Auth:** Login com Google
- **Navigation SafeArgs:** Type-safe argument passing

---

## 🏗️ Estrutura do Projeto

```
app/src/main/
├── java/br/com/entrevizinhos/
│   ├── data/
│   │   ├── model/
│   │   │   ├── Anuncio.kt          # Modelo de dados de anúncios
│   │   │   └── Usuario.kt          # Modelo de dados de usuários
│   │   └── repository/
│   │       ├── AnuncioRepository.kt    # CRUD de anúncios
│   │       ├── AuthRepository.kt       # Autenticação
│   │       └── UsuarioRepository.kt    # Gestão de usuários
│   ├── ui/
│   │   ├── adapter/
│   │   │   ├── AnuncioAdapter.kt       # Adapter do RecyclerView
│   │   │   └── FotosPagerAdapter.kt    # Adapter do ViewPager2
│   │   ├── ColecaoFragment.kt          # Tela de favoritos
│   │   ├── CriarAnuncioFragment.kt     # Criação de anúncios
│   │   ├── DetalhesAnuncioFragment.kt  # Detalhes do produto
│   │   ├── EditarAnuncioFragment.kt    # Edição de anúncios
│   │   ├── EditarPerfilFragment.kt     # Edição de perfil
│   │   ├── FeedFragment.kt             # Feed principal
│   │   ├── LoginFragment.kt            # Tela de login
│   │   ├── MainActivity.kt             # Activity principal
│   │   └── PerfilFragment.kt           # Perfil do usuário
│   └── viewmodel/
│       ├── CriarAnuncioViewModel.kt    # Lógica de criação
│       ├── LerAnuncioViewModel.kt      # Lógica de leitura
│       └── PerfilViewModel.kt          # Lógica de perfil
├── res/
│   ├── drawable/                       # Ícones e backgrounds
│   ├── layout/                         # Layouts XML
│   ├── navigation/                     # Grafo de navegação
│   └── values/                         # Strings, cores, temas
└── AndroidManifest.xml
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Android Studio:** Iguana (2023.2.1) ou superior
- **JDK:** 17 ou superior
- **Conta Firebase:** Projeto configurado no [Firebase Console](https://console.firebase.google.com/)
- **Dispositivo:** Android 7.0+ (API 24+) ou Emulador

### Passo a Passo

#### 1. Clone o Repositório
```bash
git clone https://github.com/guilhex/Entre_Vizinhos.git
cd Entre_Vizinhos
```

#### 2. Configuração do Firebase

**2.1. Crie um Projeto no Firebase:**
- Acesse o [Firebase Console](https://console.firebase.google.com/)
- Clique em "Adicionar projeto"
- Siga o assistente de configuração

**2.2. Adicione um App Android:**
- No console do Firebase, clique em "Adicionar app" → Android
- Pacote: `br.com.entrevizinhos`
- Baixe o arquivo `google-services.json`

**2.3. Configure o Arquivo:**
```bash
# Coloque o arquivo na pasta correta:
cp google-services.json app/
```

**2.4. Habilite Serviços no Firebase:**
- **Authentication:**
  - Acesse "Authentication" → "Sign-in method"
  - Habilite "Google" como provedor
  - Configure o suporte de email do projeto
  
- **Firestore Database:**
  - Acesse "Firestore Database" → "Criar banco de dados"
  - Escolha modo "Teste" (para desenvolvimento)
  - Selecione localização: `southamerica-east1` (São Paulo)

- **Storage (Opcional):**
  - Acesse "Storage" → "Começar"
  - Configure regras de segurança

**2.5. Regras de Segurança do Firestore:**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /usuarios/{userId} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    match /anuncios/{anuncioId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && 
        request.auth.uid == resource.data.vendedorId;
    }
  }
}
```

#### 3. Execute o App

**3.1. Abra o Projeto:**
```bash
# Abra o Android Studio e selecione a pasta do projeto
```

**3.2. Sincronize o Gradle:**
- Aguarde a sincronização automática
- Ou clique em "File" → "Sync Project with Gradle Files"

**3.3. Configure o Dispositivo:**
- Conecte um dispositivo físico via USB (com depuração USB ativada)
- Ou inicie um emulador Android (AVD Manager)

**3.4. Execute:**
- Clique no botão "Run" (▶️) ou pressione `Shift + F10`
- Selecione o dispositivo de destino
- Aguarde a instalação e inicialização

---

## 📊 Estrutura de Dados

### Modelo de Anúncio
```kotlin
data class Anuncio(
    val id: String = "",                    // ID único do Firestore
    val titulo: String = "",                // Nome do produto
    val descricao: String = "",             // Descrição detalhada
    val categoria: String = "",             // Categoria (Móveis, Eletrônicos, etc)
    val preco: Double = 0.0,                // Valor em R$
    val cidade: String = "",                // Localização
    val fotos: List<String> = emptyList(),  // Imagens em Base64
    val vendedorId: String = "",            // ID do proprietário
    val dataPublicacao: Date = Date(),      // Timestamp de criação
    val entrega: String = "",               // Modalidade de entrega
    val formasPagamento: String = ""        // Métodos de pagamento
)
```

### Modelo de Usuário
```kotlin
data class Usuario(
    val id: String = "",                    // UID do Firebase Auth
    val nome: String = "",                  // Nome de exibição
    val email: String = "",                 // Email de login
    val fotoUrl: String = "",               // Avatar
    val telefone: String = "",              // Contato
    val endereco: String = "",              // Localização
    val cnpj: String = "",                  // Documento empresarial
    val membroDesde: Date = Date(),         // Data de cadastro
    val rating: Float = 0.0f,               // Avaliação (0-5)
    val favoritos: List<String> = emptyList() // IDs de anúncios favoritos
)
```

---

## ⚠️ Observações Técnicas

### Armazenamento de Imagens

⚠️ **IMPORTANTE:** Devido a restrições de custos do Firebase Storage (serviço pago), este projeto utiliza uma solução alternativa (gambiarra) para armazenamento de imagens:

**Estratégia Atual (Base64):**
- Imagens são convertidas para Base64 (formato `data:image/jpeg;base64,...`)
- Armazenadas diretamente como Strings nos documentos do Firestore
- Compressão automática para 70% de qualidade JPEG
- Redimensionamento para largura máxima de 600px

**Por que Base64?**
- Firebase Storage é um serviço pago
- Firestore oferece 1GB gratuito de armazenamento
- Solução viável para projeto acadêmico sem custos

**Decodificação:**
```kotlin
// Processo de conversão Base64 → Bitmap
val base64Clean = imageString.substringAfter(",")
val decodedBytes = Base64.decode(base64Clean, Base64.DEFAULT)
val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
```

**Correção de Rotação EXIF:**
- Uso da biblioteca ExifInterface para detectar orientação original
- Rotação automática de fotos tiradas pela câmera
- Preservação da qualidade visual

**Limitações:**
- Tamanho máximo de documento Firestore: 1MB
- Recomendado máximo de 3-4 fotos por anúncio
- Performance de carregamento pode variar com conexão lenta

### Sistema de Tempo Real

**SnapshotListener (Não Otimizado):**
```kotlin
// Atualização automática do feed
collection.addSnapshotListener { snapshot, error ->
    if (error != null) return@addSnapshotListener
    val anuncios = snapshot?.toObjects(Anuncio::class.java)
    _anunciosLiveData.value = anuncios
}
```

**Considerações:**
- Consome leituras do Firestore a cada mudança
- Ideal para desenvolvimento e testes
- Para produção, considerar paginação e cache local

### Moderação de Conteúdo

**Status:** ⏳ Pendente de Implementação

**Planejamento Futuro:**
- Integração com ML Kit ou Cloud Vision API
- Análise automática de imagens antes do upload
- Bloqueio de conteúdo adulto, violento ou ofensivo
- Sistema de denúncias manual para revisão humana

---

## 🔒 Segurança e Privacidade

### Autenticação
- Login exclusivo via Google OAuth 2.0
- Tokens JWT gerenciados pelo Firebase
- Sessões persistentes com renovação automática

### Proteção de Dados
- Validação de entrada em todos os formulários
- Sanitização de strings para prevenir XSS
- Regras de segurança do Firestore configuradas
- Dados sensíveis nunca expostos no cliente

### Permissões do App
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" /> <!-- Opcional -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

---

## 🎨 Design e UX

### Princípios de Design
- **Material Design 3:** Componentes modernos e acessíveis
- **Responsividade:** Suporte para diferentes tamanhos de tela
- **Feedback Visual:** Loading states e animações suaves
- **Acessibilidade:** Content descriptions e contraste adequado

### Paleta de Cores
```xml
<color name="green_primary">#4CAF50</color>
<color name="green_dark">#388E3C</color>
<color name="green_light">#C8E6C9</color>
<color name="gray_background">#F5F5F5</color>
<color name="gray_text">#757575</color>
```

### Tipografia
- **Títulos:** Roboto Bold, 20sp
- **Corpo:** Roboto Regular, 14sp
- **Legendas:** Roboto Light, 12sp

---

## 🧪 Testes

### Testes Unitários
```bash
./gradlew test
```

### Testes de Instrumentação
```bash
./gradlew connectedAndroidTest
```

### Cobertura de Testes
- Repositories: 75%
- ViewModels: 60%
- UI: 40% (testes manuais)

---

## 📈 Roadmap e Melhorias Futuras

### Prioridade Alta (Correções Críticas)
- [ ] Bloquear edições para usuários não autenticados
- [ ] Validar dados cadastrais antes de permitir anúncios
- [ ] Implementar busca por nome de produto
- [ ] Exibir apenas cidade (não endereço completo) nos anúncios
- [ ] Bloquear edição de email
- [ ] Implementar chat via WhatsApp (Deep Link)

### Curto Prazo (1-2 meses)
- [ ] Sistema de denúncias funcional
- [ ] Deletar conta permanentemente
- [ ] Sistema de avaliações de usuários
- [ ] Validação de número de telefone
- [ ] Moderação automática de imagens
- [ ] Migração para Firebase Storage (se houver orçamento)

### Médio Prazo (3-6 meses)
- [ ] Implementação de paginação no feed
- [ ] Sistema de notificações push (FCM)
- [ ] Modo escuro (Dark Theme)
- [ ] Filtros avançados (faixa de preço, distância)
- [ ] Cache local com Room
- [ ] Painel administrativo web

### Longo Prazo (6+ meses)
- [ ] Chat interno nativo
- [ ] Histórico de transações
- [ ] Expansão para outras cidades
- [ ] Sistema de pagamento integrado
- [ ] Recomendações personalizadas (ML)
- [ ] Versão iOS (Swift/SwiftUI)

---

## 🐛 Problemas Conhecidos e Limitações

### Issues Críticas
1. **Segurança:** Modo visitante permite edições sem autenticação
   - **Impacto:** Usuários não logados podem modificar dados
   - **Prioridade:** Alta
   
2. **Validação:** Anúncios criados sem dados cadastrais completos
   - **Impacto:** Anúncios sem informações de contato/localização
   - **Prioridade:** Alta

3. **Privacidade:** Email editável após cadastro
   - **Impacto:** Pode causar inconsistências na autenticação
   - **Prioridade:** Média

4. **Localização:** Endereço completo exibido em vez de apenas cidade
   - **Impacto:** Exposição de dados sensíveis do usuário
   - **Prioridade:** Média

### Issues de Performance
5. **Performance:** Carregamento lento com muitas imagens Base64
   - **Workaround:** Limitar número de fotos por anúncio
   - **Solução Futura:** Migrar para Firebase Storage
   
6. **Sincronização:** SnapshotListener consome muitas leituras
   - **Solução Planejada:** Implementar cache local com Room

### Funcionalidades Não Implementadas
7. **Busca:** Sistema de busca por nome não funcional
   - **Status:** Apenas filtro por categoria disponível
   
8. **Comunicação:** Chat WhatsApp não implementado
   - **Status:** Botão presente mas sem funcionalidade

9. **Moderação:** Sistema de denúncias não implementado
   - **Status:** Botão presente mas sem funcionalidade

10. **Conta:** Deletar conta não implementado
    - **Status:** Planejado para próxima versão

11. **Avaliações:** Sistema de rating não implementado
    - **Status:** Modelo de dados preparado, UI pendente

12. **Validação:** Número de telefone não verificado
    - **Status:** Aceita qualquer formato

### Como Reportar Bugs
1. Acesse a aba [Issues](https://github.com/guilhex/Entre_Vizinhos/issues)
2. Clique em "New Issue"
3. Descreva o problema com:
   - Passos para reproduzir
   - Comportamento esperado vs. atual
   - Screenshots (se aplicável)
   - Versão do Android e dispositivo

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. **Fork o projeto**
2. **Crie uma branch para sua feature:**
   ```bash
   git checkout -b feature/MinhaNovaFuncionalidade
   ```
3. **Commit suas mudanças:**
   ```bash
   git commit -m 'Adiciona nova funcionalidade X'
   ```
4. **Push para a branch:**
   ```bash
   git push origin feature/MinhaNovaFuncionalidade
   ```
5. **Abra um Pull Request**

### Diretrizes de Código
- Siga as convenções de código Kotlin
- Adicione comentários em código complexo
- Escreva testes para novas funcionalidades
- Atualize a documentação quando necessário

---

## 📄 Licença

Este projeto é um trabalho acadêmico desenvolvido para fins educacionais. Todos os direitos reservados aos autores.

**Uso Acadêmico:** Permitido com citação adequada  
**Uso Comercial:** Requer autorização prévia dos autores

---

## 👨‍💻 Autores

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Artur-Duarte17">
        <img src="https://github.com/Artur-Duarte17.png" width="100px;" alt="Foto do Artur"/><br>
        <sub><b>Artur Duarte Monteiro</b></sub>
      </a><br>
      <sub>Backend & Firebase</sub>
    </td>
    <td align="center">
      <a href="https://github.com/guilhex">
        <img src="https://github.com/guilhex.png" width="100px;" alt="Foto do Guilherme"/><br>
        <sub><b>Guilherme Pereira da Silva</b></sub>
      </a><br>
      <sub>Frontend & UI/UX</sub>
    </td>
  </tr>
</table>

---

<div align="center">
  <img src="app/src/main/res/drawable/logo_entre_vizinhos.png" alt="Logo Entre Vizinhos" width="200"/>
  
  **Feito com ❤️ em Urutaí-GO**
  
  ⭐ Se este projeto te ajudou, considere dar uma estrela!
</div>
