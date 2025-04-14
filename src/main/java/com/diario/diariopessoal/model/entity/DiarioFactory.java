package com.diario.diariopessoal.model.entity;

//classe para criação de diários diferentes
public class DiarioFactory {
    // utilização de métodos estaticos
    // criação de diario com descrição padrao
    public static DiarioTexto criarDiarioTexto(Usuario usuario) {
        // instanciação de classe
        return new DiarioTexto("Meu Diário", "Um lugar para registrar meus pensamentos", usuario);
    }

    // diario completamente personalizado
    public static DiarioTexto criarDiarioTexto(String nome, String descricao, Usuario usuario) {
        return new DiarioTexto(nome, descricao, usuario);
    }

    public static DiarioPremium criarDiarioPremium(Usuario usuario) {
        if (!(usuario instanceof UsuarioPremium)) {
            throw new IllegalArgumentException("Apenas usuários premium podem criar diários premium");
        }
        return new DiarioPremium("Meu Diário Premium", "Registre suas memórias com recursos exclusivos", usuario);
    }

    public static DiarioPremium criarDiarioPremium(String nome, String descricao, Usuario usuario) {
        if (!(usuario instanceof UsuarioPremium)) {
            throw new IllegalArgumentException("Apenas usuários premium podem criar diários premium");
        }
        return new DiarioPremium(nome, descricao, usuario);
    }

    public static DiarioBase criarDiarioParaUsuario(Usuario usuario) {
        // Cria o tipo apropriado de diário com base no tipo de usuário
        if (usuario instanceof UsuarioPremium) {
            return criarDiarioPremium(usuario);
        } else {
            return criarDiarioTexto(usuario);
        }
    }

}
