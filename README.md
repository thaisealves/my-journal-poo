# Projeto Diário Pessoal - Documentação

## 📝 Visão Geral

Diário Pessoal é um sistema de gerenciamento de diários que permite aos usuários registrar, organizar e visualizar suas reflexões pessoais. O sistema implementa um modelo de assinatura que distingue entre usuários regulares e premium, oferecendo funcionalidades adicionais para os assinantes premium.

## 🧩 Principais Funcionalidades

- Criação e gerenciamento de diários
- Adição de entradas em diários com conteúdo de texto
- Categorização de entradas
- Registro de humor para cada entrada
- Funcionalidades premium: adição de conteúdo multimídia (imagens, vídeos, áudios, links)

## 🏗️ Arquitetura e Design do Sistema

### Diagrama de Classes UML

![Diagrama de Classes UML](src/main/resources/static/images/diagrama-uml.png)

[LINK DO DIAGRAMA UML](https://www.plantuml.com/plantuml/png/pLXVRzis47_tfz2oXoPj4hJRcr44LMqr4nR_WIqFmuQXk4XCIamIFP8ArXRzCCCUXkrhv8kDyabdqgATQ7-0vY6Mxeu_tl5-ToJrIccGkYhomHVw7IjOK81K0_KpBzSWeGZI79GAjPRykj8iJaMvv1zOy7mmU1B4fMRo1bAc1hnz3EPCAguqCuBWuo0mVyzkcOwpuvDXS2NAMnIfZOXdA3EgEy6pmQU11JJc8BbOCdd7Ku9IaD-1Z4ej8ODZXh_3e14qE6YQ6o_8L6scDvANZU02ZQuhbPBHN4Z7gKQCbh26MxBqUVua6DLhnLG0ryfOQL03V4erBcFj9iyWOwz1iO0NwvmL9WJbMRClsmdmBJDe8yaX1L6BKyZ7e5d22zPlXSbwl0N4OHt8Zwtldu8xo8tRiZQy5Y9dK9fuTsF3T9d8A8HI56mOBBNa50PJgUHcJbTSgSgCDS8LFg03My3Zaux4nZuD75mKxM8YX1NKCyXuoaMvpQdXjcPLHY2xQpOLXN45jRJAA7RywQnrwrrdiArpOl31O20rYBjEQglg1ErZfmapbQncfvsuXFsgHM3-iJ9hPJiqzLRa6MiAp5jU5hQJH1Ivg-PNzqzopymUiAPvKj3iLaWEms3KFluN6AV1kweGSXXyK_yGegVH_LyjBfYjTUruJeCUr-qSximbAtXL_1lsHANalrGivOUOH3CvN2BXPqsNqq3pjHYHRMAz6SzaFkepAIsNPnCaNI-oqAX7ygOxx374UMJ3wh7N4nTRER7Gsco9RhkvGgalNwY1IWj9tg7CBbbqj7NnX5xzD3giFWtSsNgQx-bIj7sq_EfkGgHwS0Sas1g4NhOufFQwiWCMrkxxTZDKkD7j3ZQhZuEjDAXMlBUf-WyoH1-7qCGaKhKasb_8gFSKCoge9Kruni7H4JDVyn8hulgzPZ_-X7vxglx4h_4IfM_dQHQBINvZl9MO0cgptPfrKuFuH3PdFagKOxJVlyuPIEO7U6nhkt7vpsMKZWvUny2VlawDk8aqi4ugLTTYJ-eVOkxXSg7coCggSDQqeN4IVP_CDazNyVJRj_4al8mcs_Vlud5agSFLEBPUnxFHQX9D6u3HR9f4g_7iRJHvlKhYyQoT5qkmcV1DT1N_W5ClucKIRHx3wJAUBH4cdYxdyI8SuziqMYMBHh5On4ahlro4oOpAbjhBuRvFHuDkBUwMeNHfOBMyNS43vusJp-NYC1Ao0u1IAzf7iUsZqg1v-Yam4uAabPX39j594b62MaOUJ4-bc2nXDqMi09wx8iNATx2JI5x3juV6xT7p0jLh9czgbFdcrzbgw4Bblf8pb5fqX5x92JVBAnpdPXfLKGtWaQDJHk5pJv7U6uF_V8Zn7-EY3AsJU8TRk6ncxkx28MIkcGIvDC7MLB-3pTD8yhKMjn9k88D7SgV31VU-GEAM3Hbe29NY9UXAWlLneibe3cMpJMojA8NEBS4dxP5VVFNEjqtxsF7M-PYopNCDOLHUfyMrPdNUr88f3QLccwJmKgEQg-s0KFCxw5IbP7LwfEM4UHTpdzjEfZP5tEiAPOOxzeJJlPWSlXZzuxF9WPi7dLUwwzNT3BSs3UlCRhOChuvVdBGxr2HQ9WiZ-HebuNHLgxywQJXgHgOWh3ovgCUsWKKMbBO9fiAuP9ALAOTCD0bHBnFFTVh2EEislGciNxPBcLpucu6RrBOr90uN92l47QCH1pOblqbYtp5tUqjouNw1gZJFkRx_JVATZr1l8DL2lc-YfDs6c4fVXZndK4lj7zt_kr9l-0D7PCVWOOJUBlQO0YGSk7zlsRQbPrs5a0dwUD19a1OQybQBt7ODU9aQlvY18H2gIzSoOsJPJhl7KZB5DFYrU1EnlQAQmoxWRgPDG8leSdKLBZRFyqKqYLSJPEuYkWf7yMmQRWwzovhBebWBnU__kFzT3EnFg4SlZeAdPsV1qTCZkZWEWxeNtlzP39mS7RQjvmrl9Ri5dAGrE7f-Vcx62JCE7wqpy91Qx0V8X44gn013HCS4JcObsGmnSQYA4pxPcLyylmzDArfBfXHiV59Af7pNAGDwTdQn6THEECGl58DkmwvDtNkXkqUZtjqhBPVzawsPrAnCMG0KiZ5oyrpxFsPhLcRtVzNzTk3sdVFp2xlGw-MKUut8szRibO6jYlnl)

### Conceitos de POO Implementados

#### 1. Classificação / Instanciação

O sistema utiliza o conceito de classificação para definir as entidades e suas instanciações quando necessário.

**Exemplo:**

```java
public static DiarioTexto criarDiarioTexto(String nome, String descricao, Usuario usuario) {
    return new DiarioTexto(nome, descricao, usuario);
}
```

#### 2. Encapsulamento

Atributos privados com getters e setters para controlar o acesso aos dados.
**Exemplo:**

```java
@NotBlank
@Size(min = 3, max = 50)
@Column(nullable = false, unique = true, length = 50)
private String username;

public String getUsername() {
    return username;
}

public void setUsername(String username) {
    this.username = username;
}
```

#### 3. Generalização / Especialização

O sistema usa herança para especializar tipos de usuários e diários.
**Exemplo:**

```java
@Entity
@Table(name = "usuario_premium")
public class UsuarioPremium extends Usuario {
    // Atributos e métodos específicos para usuários premium
}
```

#### 4. Associação

Relacionamentos entre classes são definidos através de associações.
**Exemplo:**

```java
@NotNull
@ManyToOne
@JoinColumn(name = "categoria_id")
private Categoria categoria;

@NotNull
@ManyToOne
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario autor;
```

#### 5. Composição

Relação forte onde o componente faz parte do composto e não existe independentemente.

**Exemplo:**

```java
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
@JoinColumn(name = "diario_id")
private List<EntradaEnriquecida> entradasEnriquecidas = new ArrayList<>();
```

#### 6. Agregação

Relação onde os componentes podem existir independentemente do agregador.
**Exemplo:**

```java
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
@JoinColumn(name = "diario_id")
private List<Entrada> entradas = new ArrayList<>();
```

#### 7. Coleções

Uso de estruturas de dados para armazenar conjuntos de objetos.
**Exemplo:**

```java
private Map<String, T> diarios = new HashMap<>();
```

#### 8. Classes / Métodos Genéricos

Implementação de classes e métodos que trabalham com tipos parametrizados.
**Exemplo:**

```java
public class GerenciadorDiarios<T extends DiarioBase> {
    // Implementação com uso de tipo genérico T

    public void adicionarDiario(String nome, T diario) {
        // Método utilizando tipo genérico
    }
}
```

#### 9. Classes Abstratas

Classes que não podem ser instanciadas diretamente e fornecem implementação parcial.

**Exemplo:**

```java
@MappedSuperclass
public abstract class EntidadeBase implements Persistente {
    // Implementação comum a todas as entidades

    public abstract boolean validar();
}
```

#### 10. Interfaces

Definição de contratos que as classes implementadoras devem seguir.
**Exemplo:**

```java
public interface DiarioService {
    public void salvarEntrada(Entrada entrada);
    public List<Entrada> listarEntradas();
    public Entrada buscarEntradaPorId(Long id);
    public void apagarEntrada(Long id);
}
```

#### 11. Atributos e Métodos Estáticos

Elementos que pertencem à classe e não às instâncias.

**Exemplo:**

```java
public static Categoria criarCategoriaPadrao() {
    return new Categoria("Padrão", "Categoria padrão", "#FFFFFF");
}
```

#### 12. Herança

Reutilização de código através de hierarquias de classes.

**Exemplo:**

```java
@Entity
@Table(name = "diario_texto")
public class DiarioTexto extends DiarioBase implements DiarioService {
    // Implementação específica que herda de DiarioBase
}
```

#### 13. Sobrescrita de métodos

Reimplementação de métodos herdados para comportamento específico.

**Exemplo:**

```java
@Override
public void setUsuario(Usuario usuario) {
    if (!(usuario instanceof UsuarioPremium)) {
        throw new IllegalStateException("Apenas usuários premium podem possuir diários premium");
    }
    super.setUsuario(usuario);
}
```

#### 14. Sobrecarga

Métodos com o mesmo nome, mas diferentes parâmetros.

**Exemplo:**

```java
public void adicionarEntrada(String titulo, String conteudo) {
    Entrada entrada = new Entrada(titulo, conteudo, Categoria.criarCategoriaPadrao(), getUsuario());
    entradas.add(entrada);
}

public void adicionarEntrada(String titulo, String conteudo, Categoria categoria) {
    Entrada entrada = new Entrada(titulo, conteudo, categoria, getUsuario());
    entradas.add(entrada);
}

public void adicionarEntrada(String titulo, String conteudo, Categoria categoria, Humor humor) {
    Entrada entrada = new Entrada(titulo, conteudo, categoria, getUsuario());
    entrada.setHumor(humor);
    entradas.add(entrada);
}
```

## 🔧 Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- H2 Database (desenvolvimento)
- Bootstrap 5
  Maven

## 🔒 Segurança

O sistema implementa mecanismos de segurança para proteger os dados dos usuários:

- Armazenamento seguro de senhas com hashing
- Controle de acesso baseado em autenticação
- Validação de entrada de dados
- Verificação de autorização para recursos premium

## 📊 Modelo de Negócio

### Tipos de Usuários

1. Usuários Regulares

- Acesso a diários de texto básicos
- Categorização de entradas
- Registro de humor

2. Usuários Premium

- Todos os recursos dos usuários regulares
- Criação de diários premium
- Adição de conteúdo multimídia (imagens, vídeos, áudios)
- Links externos
- Planos de assinatura: Mensal, Trimestral e Anual

## 🚀 Como Executar o Projeto

1. Clone o repositório
2. Navegue até o diretório do projeto
3. Execute mvn clean install
4. Execute mvn spring-boot:run
5. Acesse a aplicação em http://localhost:8080
