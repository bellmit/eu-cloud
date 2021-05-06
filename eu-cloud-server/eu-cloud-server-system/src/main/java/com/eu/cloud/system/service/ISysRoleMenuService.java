package com.eu.cloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eu.cloud.server.system.api.pojo.po.SysRoleMenu;

import java.util.List;

public interface ISysRoleMenuService extends IService<SysRoleMenu> {

    List<Long> listMenuIds(Long roleId);

    /**
     * 修改角色菜单
     * @param roleId
     * @param menuIds
     * @return
     */
    boolean update(Long roleId, List<Long> menuIds);
}
