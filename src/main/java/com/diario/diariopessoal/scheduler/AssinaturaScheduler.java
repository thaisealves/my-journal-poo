package com.diario.diariopessoal.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.diario.diariopessoal.model.entity.Usuario;
import com.diario.diariopessoal.model.entity.UsuarioPremium;
import com.diario.diariopessoal.repository.UsuarioRepository;
import com.diario.diariopessoal.service.UsuarioService;

import java.util.List;

@Component
@EnableScheduling
public class AssinaturaScheduler {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UsuarioService usuarioService;

    // Executar diariamente à meia-noite
    @Scheduled(cron = "0 0 0 * * ?")
    public void verificarAssinaturasExpiradas() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        for (Usuario usuario : usuarios) {
            if (usuario instanceof UsuarioPremium) {
                UsuarioPremium premium = (UsuarioPremium) usuario;
                
                if (premium.obterDiasRestantes() <= 0) {
                    System.out.println("Assinatura expirada para usuário: " + premium.getUsername());
                    
                    // Downgrade para usuário normal
                    try {
                        usuarioService.downgradeToPadrao(premium.getId());
                        System.out.println("Usuário rebaixado para conta normal: " + premium.getUsername());
                    } catch (Exception e) {
                        System.err.println("Erro ao rebaixar usuário: " + e.getMessage());
                    }
                    
                }
            }
        }
    }
}