package com.system.email.infra.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.system.email.infra.gateway.redis.RedisOAuth2AuthorizedClient;
import com.system.email.infra.model.auth.OAuth2AuthorizedClientDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 *
 * The Redis configuration.
 *
 * <p>This class contains the Redis configurations.<p/>
 *
 * @author Marcus Nastasi
 * @version 1.0.2
 * @since 2025
 * */
@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.host}")
    private String hostName;
    @Value("${spring.redis.port}")
    private int port;

    /**
     *
     * The redis template config.
     *
     * <p>This method configures a Redis template to handle saving on a specific types of key and value.<p/>
     *
     * @param factory The redis connection factory base object.
     *
     * @return Return a template to represent the saving of a String as key, and OAuth2AuthorizedClientDto as value.
     */
    @Bean
    public RedisTemplate<String, OAuth2AuthorizedClientDto> redisTemplate(RedisConnectionFactory factory) {
        // Creating a new RedisTemplate object.
        RedisTemplate<String, OAuth2AuthorizedClientDto> template = new RedisTemplate<>();
        template.setConnectionFactory(factory); // Setting the connection factory.
        // Creating and config the ObjectMapper to JSON.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // Using jackson 2 serializer to JSON to Redis.
        Jackson2JsonRedisSerializer<OAuth2AuthorizedClientDto> serializer = new Jackson2JsonRedisSerializer<>(
                objectMapper,
                OAuth2AuthorizedClientDto.class
        );
        // Configuring the serializer to the template.
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        return template;
    }

    /**
     *
     * The explicit configuration to Redis connection.
     *
     * <p>This method explicit configure Redis connection factory.<p/>
     *
     * @return An object of type LettuceConnectionFactory.
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(hostName);
        redisStandaloneConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisOAuth2AuthorizedClient redisOAuth2AuthorizedClient(RedisTemplate<String, OAuth2AuthorizedClientDto> redisTemplate, ClientRegistrationRepository clientRegistrationRepository) {
        return new RedisOAuth2AuthorizedClient(redisTemplate, clientRegistrationRepository);
    }
}
