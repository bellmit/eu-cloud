package com.eu.cloud.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eu.cloud.core.constants.AuthConstants;
import com.eu.cloud.redis.utils.RedisUtil;
import com.eu.cloud.server.system.api.pojo.po.SysPermission;
import com.eu.cloud.system.mapper.SysPermissionMapper;
import com.eu.cloud.system.service.ISysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<SysPermission> listPermissionRoles() {
        return this.baseMapper.listPermissionRoles();
    }

    @Override
    public IPage<SysPermission> list(Page<SysPermission> page, SysPermission permission) {
        List<SysPermission> list = this.baseMapper.list(page, permission);
        page.setRecords(list);
        return page;
    }

    @Override
    public boolean refreshPermissionRolesCache() {
        redisUtil.del(AuthConstants.PERMISSION_ROLES_KEY);
        List<SysPermission> permissions = this.listPermissionRoles();
        Map<String, List<String>> permissionRoles = new TreeMap<>();
        Optional.ofNullable(permissions).orElse(new ArrayList<>()).forEach(permission -> {
            // 转换 roleId -> ROLE_{roleId}
            List<String> roles = Optional.ofNullable(permission.getRoleIds())
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(roleId -> AuthConstants.AUTHORITY_PREFIX + roleId)
                    .collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(roles)) {
                permissionRoles.put(permission.getMethod() + "_" + permission.getPerm(), roles);
            }
            redisUtil.hmset(AuthConstants.PERMISSION_ROLES_KEY, permissionRoles);
        });
        return true;
    }

    @Override
    public List<String> listPermsByRoleIds(List<Long> roleIds, Integer type) {
        return this.baseMapper.listPermsByRoleIds(roleIds, type);
    }
}
