package com.eu.cloud.gateway.loader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 动态更新路由网关service
 * 1）实现一个Spring提供的事件推送接口ApplicationEventPublisherAware
 * 2）提供动态路由的基础方法，可通过获取bean操作该类的方法。该类提供新增路由、更新路由、删除路由，然后实现发布的功能。
 *
 * @author jiangxd
 */
@Slf4j
@Service
public class DynamicRouteService implements ApplicationEventPublisherAware {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 更新路由
     *
     * @param definitions
     * @return
     */
    public String updateRoute(List<RouteDefinition> definitions) {
        // 包括配置文件中的路由
        List<RouteDefinition> routeDefinitionsExits = Optional.ofNullable(this.routeDefinitionLocator.getRouteDefinitions().collectList().block()).orElse(new ArrayList<>());
        // 清空所有路由(清除的只能是内存中的)
        routeDefinitionsExits.forEach(routeDefinition -> delete(routeDefinition.getId()));
        // 添加新的路由
        definitions.forEach(this::add);
        return "success";
    }

    /**
     * 增加路由
     *
     * @param definition
     * @return
     */
    public String add(RouteDefinition definition) {
        log.info("gateway add route {}", definition);
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }

    /**
     * 删除路由
     *
     * @param id
     * @return
     */
    public String delete(String id) {
        try {
            log.info("gateway delete route id {}", id);
            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "delete success";
        } catch (Exception e) {
            return "delete fail";
        }
    }

}
