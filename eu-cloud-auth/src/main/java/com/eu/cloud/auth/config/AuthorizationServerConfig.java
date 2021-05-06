package com.eu.cloud.auth.config;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.eu.cloud.auth.constants.CacheConstants;
import com.eu.cloud.auth.pojo.User;
import com.eu.cloud.auth.service.JdbcClientDetailsServiceImpl;
import com.eu.cloud.auth.service.UserDetailsServiceImpl;
import com.eu.cloud.core.constants.AuthConstants;
import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 授权服务配置
 */
@Configuration
@EnableAuthorizationServer
@AllArgsConstructor
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final DataSource dataSource;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final RedisConnectionFactory redisConnectionFactory;


    /**
     * 配置客户端详情(数据库)
     */
    @Override
    @SneakyThrows
    public void configure(ClientDetailsServiceConfigurer clients) {
        JdbcClientDetailsServiceImpl jdbcClientDetailsService = new JdbcClientDetailsServiceImpl(dataSource);
        jdbcClientDetailsService.setFindClientDetailsSql(AuthConstants.FIND_CLIENT_DETAILS_SQL);
        jdbcClientDetailsService.setSelectClientDetailsSql(AuthConstants.SELECT_CLIENT_DETAILS_SQL);
        clients.withClientDetails(jdbcClientDetailsService);
    }

    /**
     * 配置授权（authorization）以及令牌（token）的访问端点和令牌服务(token services)
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                .tokenStore(tokenStore())
                .authenticationManager(authenticationManager)
                .tokenEnhancer(tokenEnhancer())
                .userDetailsService(userDetailsService)
                // refresh token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
                //      1 重复使用：access token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准
                //      2 非重复使用：access token过期刷新时， refresh token过期时间延续，在refresh token有效期内刷新便永不失效达到无需再次登录的目的
                .reuseRefreshTokens(true);
//                .pathMapping("/oauth/confirm_access", "/token/confirm_access");
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients().checkTokenAccess("permitAll()");
    }

    @Bean
    public TokenStore tokenStore() {
        RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
        tokenStore.setPrefix(CacheConstants.PROJECT_OAUTH_ACCESS);
        return tokenStore;
    }

    /**
     * 自定义认证异常响应数据
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            response.setStatus(HttpStatus.HTTP_OK);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Cache-Control", "no-cache");
            GlobalResponseWrapper result = new GlobalResponseWrapper(GlobalExceptionCode.CLIENT_AUTHENTICATION_FAILED);
            response.getWriter().print(JSONUtil.toJsonStr(result));
            response.getWriter().flush();
        };
    }

    /**
     * JWT内容增强
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> map = new HashMap<>(3);
            User user = (User) authentication.getUserAuthentication().getPrincipal();
            map.put(AuthConstants.USER_ID_KEY, user.getId());
            map.put(AuthConstants.CLIENT_ID_KEY, user.getClientId());
            map.put(AuthConstants.USER_NAME_KEY, user.getUsername());
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
            return accessToken;
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false); // 用户不存在异常抛出
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * 密码编码器
     * 密码判读 DaoAuthenticationProvider#additionalAuthenticationChecks
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
