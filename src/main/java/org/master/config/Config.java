/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.config;

import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

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
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
            endpoints.tokenStore(tokenStore()).tokenEnhancer(tokenEnhancerChain);
        }

        @Bean
        public TokenEnhancer tokenEnhancer() {
            return new CustomTokenEnhancer();
        }

        @Bean
        public TokenStore tokenStore() {
//            return new JdbcTokenStore(dataSource());
            return new JwtTokenStore(accessTokenConverter()) {

                @Override
                public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
                    super.storeRefreshToken(refreshToken, authentication); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
                    super.storeAccessToken(token, authentication); //To change body of generated methods, choose Tools | Templates.
                }
            };
        }

        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            converter.setSigningKey("123");
            return converter;
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
}
