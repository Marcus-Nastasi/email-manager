package com.system.email.infra.config.gson;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The gson config.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 */
@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
