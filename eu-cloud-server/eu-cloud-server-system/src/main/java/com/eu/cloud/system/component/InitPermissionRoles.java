package com.eu.cloud.system.component;

import com.eu.cloud.system.service.ISysPermissionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 容器启动完成时加载角色权限规则至Redis缓存
 */
@Component
@AllArgsConstructor
@Slf4j
public class InitPermissionRoles implements CommandLineRunner {

    private ISysPermissionService iSysPermissionService;

    @Override
    public void run(String... args) {
        iSysPermissionService.refreshPermissionRolesCache();
    }
}
