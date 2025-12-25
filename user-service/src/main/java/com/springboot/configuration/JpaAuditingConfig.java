package com.springboot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
//@Profile("!test")   // disable in tests
public class JpaAuditingConfig {
}
