package com.diario.diariopessoal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private UserDetailsService userDetailsService;

        @Bean
        public static PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // Desabilitar CSRF (Cross Site Request Forgery)
                                .csrf(csrf -> csrf
                                                // Desabilitar apenas para o console H2
                                                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
                                .authorizeHttpRequests(authorize -> authorize
                                                // URLs públicas
                                                .requestMatchers("/", "/login", "/usuarios/cadastro",
                                                                "/usuarios/salvar")
                                                .permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                                .requestMatchers("/diarios/**", "/assinatura/**").authenticated()

                                                // Permitir acesso ao console H2
                                                .requestMatchers("/h2-console/**").permitAll()
                                                // URLs que requerem autenticação
                                                .anyRequest().authenticated())
                                // Configurar segurança de cabeçalhos
                                .headers(headers -> headers
                                                // Desabilitar X-Frame-Options para permitir frames no console H2
                                                .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                                // Configurar login customizado
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/diarios", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                // Configurar logout
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .permitAll())
                                // Tratamento de acesso negado
                                .exceptionHandling(exceptions -> exceptions
                                                .accessDeniedPage("/403"));

                return http.build();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
                auth
                                .userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder());
        }
}
