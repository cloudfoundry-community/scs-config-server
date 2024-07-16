package com.starkandwayne.springcloudconfigserver.dashboard;

import java.util.HashMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

@Order(0)
@Configuration
@EnableWebSecurity
@Profile({"!test", "dashboardTest"})
public class AssetsConfiguration {
   private final ApplicationContext applicationContext;
   private String ASSETS_URL_PREFIX = "/assets/**";

   public AssetsConfiguration(ApplicationContext applicationContext) {
      this.applicationContext = applicationContext;
   }

   @Bean
   public HandlerMapping staticAssetsHandler() {
      SimpleUrlHandlerMapping resourceHandlerMapping = (SimpleUrlHandlerMapping)this.applicationContext.getBean("resourceHandlerMapping", SimpleUrlHandlerMapping.class);
      HashMap<String, Object> mappings = new HashMap<String, Object>();
      resourceHandlerMapping.getUrlMap().forEach((url, handler) -> {
         mappings.put(this.ASSETS_URL_PREFIX, handler);
      });
      SimpleUrlHandlerMapping assetsHandlerMapping = new SimpleUrlHandlerMapping();
      assetsHandlerMapping.setUrlMap(mappings);
      assetsHandlerMapping.setOrder(Integer.MIN_VALUE);
      return assetsHandlerMapping;
   }

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
           .securityMatcher (ASSETS_URL_PREFIX)
           .headers(headers -> headers.cacheControl(cache -> cache.disable()))
           .authorizeHttpRequests(authorize -> authorize.requestMatchers(ASSETS_URL_PREFIX).permitAll());
       return http.build();
   }
}