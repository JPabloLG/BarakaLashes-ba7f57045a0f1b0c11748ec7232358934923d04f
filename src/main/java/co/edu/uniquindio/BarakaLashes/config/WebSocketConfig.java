package co.edu.uniquindio.BarakaLashes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un "broker" simple en memoria para enviar mensajes a /topic o /queue
        config.enableSimpleBroker("/topic", "/queue");

        // Prefijo para los mensajes enviados desde el cliente hacia el servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint al que los clientes se conectarán
        registry.addEndpoint("/citas/notificaciones")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Para compatibilidad con navegadores antiguos
    }
}
