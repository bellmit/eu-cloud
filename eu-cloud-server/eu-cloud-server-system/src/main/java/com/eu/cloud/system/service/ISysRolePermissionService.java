package com.eu.cloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eu.cloud.server.system.api.pojo.dto.RolePermissionDTO;
import com.eu.cloud.server.system.api.pojo.po.SysRolePermission;

import java.util.List;

public interface ISysRolePermissionService extends IService<SysRolePermission> {

    List<Long> listPermissionIds(Long moduleId,Long roleId, Integer type);
    List<Long> listPermissionIds(Long roleId, Integer type);
    boolean update(RolePermissionDTO rolePermission);

}
