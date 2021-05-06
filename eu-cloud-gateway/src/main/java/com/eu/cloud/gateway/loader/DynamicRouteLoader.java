package com.eu.cloud.gateway.loader;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.eu.cloud.gateway.config.GatewayRoutersConfiguration;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 动态路由加载器
 *
 * @author jiangxd
 */
@Slf4j
@Component
@DependsOn({"gatewayRoutersConfiguration"})
public class DynamicRouteLoader {

    private ConfigService configService;

    @Autowired
    private DynamicRouteService dynamicRouteService;


    @PostConstruct
    public void init() {
        log.info("初始化路由");
        this.loadRoutesByNacos();
    }

    /**
     * 刷新路由
     *
     * @return
     */
    public Mono<Void> refresh() {
        this.init();
        return Mono.empty();
    }

    /**
     * 从nacos中读取路由配置
     *
     * @return
     */
    private void loadRoutesByNacos() {
        List<RouteDefinition> routes = Lists.newArrayList();
        configService = createConfigService();
        if (configService == null) {
            log.warn("initConfigService fail");
        }
        try {
            String configInfo = configService.getConfig(GatewayRoutersConfiguration.DATA_ID, GatewayRoutersConfiguration.ROUTE_GROUP, GatewayRoutersConfiguration.DEFAULT_TIMEOUT);
            if (StrUtil.isNotBlank(configInfo)) {
                log.info("获取网关当前配置:\r\n{}", configInfo);
                routes = JSON.parseArray(configInfo, RouteDefinition.class);
            }
        } catch (NacosException e) {
            log.error("初始化网关路由时发生错误", e);
            e.printStackTrace();
        }
        for (RouteDefinition definition : routes) {
            log.info("init route : {}", definition.toString());
            dynamicRouteService.add(definition);
        }
        this.dynamicRouteByNacosListener(GatewayRoutersConfiguration.DATA_ID, GatewayRoutersConfiguration.ROUTE_GROUP);
    }

    /**
     * 监听Nacos下发的动态路由配置
     *
     * @param dataId
     * @param group
     */
    public void dynamicRouteByNacosListener(String dataId, String group) {
        try {
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("进行网关更新:\n\r{}", configInfo);
                    List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
                    dynamicRouteService.updateRoute(definitionList);
                }

                @Override
                public Executor getExecutor() {
                    log.info("getExecutor\n\r");
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("从nacos接收动态路由配置出错!!!", e);
        }
    }

    /**
     * 创建ConfigService
     *
     * @return
     */
    private ConfigService createConfigService() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", GatewayRoutersConfiguration.SERVER_ADDR);
            properties.setProperty("namespace", GatewayRoutersConfiguration.NAMESPACE);
            properties.setProperty("username", GatewayRoutersConfiguration.USERNAME);
            properties.setProperty("password", GatewayRoutersConfiguration.PASSWORD);
            return configService = NacosFactory.createConfigService(properties);
        } catch (Exception e) {
            log.error("创建ConfigService异常", e);
            return null;
        }
    }

}
