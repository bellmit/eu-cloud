package com.eu.cloud.gateway.config;

//import com.eu.cloud.gateway.handle.HystrixFallbackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.annotation.Resource;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * 路由配置信息
 *
 * @author jiangxd
 */
@Slf4j
@Configuration
public class GatewayRoutersConfiguration {

    public static final long DEFAULT_TIMEOUT = 30000;

    public static final String DATA_ID = "gateway-router1";

    public static final String ROUTE_GROUP = "DEFAULT_GROUP";

    public static String SERVER_ADDR;

    public static String NAMESPACE;

    public static String USERNAME;

    public static String PASSWORD;


    @Value("${spring.cloud.nacos.config.server-addr}")
    public void setServerAddr(String serverAddr) {
        SERVER_ADDR = serverAddr;
    }

    @Value("${spring.cloud.nacos.config.namespace}")
    public void setNamespace(String namespace) {
        NAMESPACE = namespace;
    }

    @Value("${spring.cloud.nacos.config.username}")
    public void setUsername(String username) {
        USERNAME = username;
    }

    @Value("${spring.cloud.nacos.config.password}")
    public void setPassword(String password) {
        PASSWORD = password;
    }


//    /**
//     * 路由断言
//     *
//     * @return
//     */
//    @Bean
//    public RouterFunction routerFunction() {
//        return RouterFunctions.route(
//                RequestPredicates.path("/globalFallback").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), hystrixFallbackHandler);
//
//    }
//
//    /**
//     * 映射接口文档默认地址（通过9999端口直接访问）
//     *
//     * @param indexHtml
//     * @return
//     */
//    @Bean
//    public RouterFunction<ServerResponse> indexRouter(@Value("classpath:/META-INF/resources/doc.html") final org.springframework.core.io.Resource indexHtml) {
//        return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).syncBody(indexHtml));
//    }
//
//    @Resource
//    private HystrixFallbackHandler hystrixFallbackHandler;

}
