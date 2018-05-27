/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

public class Config {

    public static final String OAUTH_CLIENT_ID = "oauth_client";
    public static final String OAUTH_CLIENT_SECRET = "oauth_client_secret";
    public static final String RESOURCE_ID = "my_resource_id";
    public static final String[] SCOPES = {"read", "write"};

    @Configuration
    @EnableAuthorizationServer
    @PropertySources({
        @PropertySource("classpath:/sys.properties")
    })
    static class OAuthAuthorizationConfig extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private Environment env;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

            clients.jdbc(dataSource());
//                    .withClient(OAUTH_CLIENT_ID)
//                    .secret(OAUTH_CLIENT_SECRET)
//                    .resourceIds(RESOURCE_ID)
//                    .scopes(SCOPES)
//                    .authorities("ROLE_USER")
//                    .authorizedGrantTypes("authorization_code", "refresh_token")
//                    .redirectUris("http://www.baidu.com")
//                    .accessTokenValiditySeconds(60 * 30) // 30min  
//                    .refreshTokenValiditySeconds(60 * 60 * 24); // 24h  
        }

        @Override
        public void configure(
                AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                    .tokenStore(tokenStore());
        }

        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(dataSource());
        }

        private DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(env.getProperty("mysql.dirver"));
            dataSource.setUrl(env.getProperty("mysql.jdbcUrl"));
            dataSource.setUsername(env.getProperty("mysql.username"));
            dataSource.setPassword(env.getProperty("mysql.password"));
            return dataSource;
        }

    }

    @Configuration
    @EnableResourceServer
    static class OAuthResourceConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId(RESOURCE_ID);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api/**").access("#oauth2.hasScope('read')")
                    .antMatchers(HttpMethod.POST, "/api/**").access("#oauth2.hasScope('write')");
        }
    }

    @Configuration
    @EnableWebSecurity
    static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("user").password("123").roles("USER")
                    .and()
                    .withUser("admin").password("123").roles("ADMIN");

        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
            http.authorizeRequests()
                    .antMatchers("/oauth/authorize").authenticated()
                    .and()
                    .httpBasic().realmName("OAuth Server");
        }
    }

}
