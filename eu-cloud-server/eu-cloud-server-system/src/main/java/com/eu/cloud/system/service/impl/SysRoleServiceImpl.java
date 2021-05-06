package com.eu.cloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eu.cloud.core.exception.GlobalException;
import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.eu.cloud.server.system.api.pojo.po.SysRole;
import com.eu.cloud.server.system.api.pojo.po.SysRoleMenu;
import com.eu.cloud.server.system.api.pojo.po.SysRolePermission;
import com.eu.cloud.server.system.api.pojo.po.SysUserRole;
import com.eu.cloud.system.mapper.SysRoleMapper;
import com.eu.cloud.system.service.ISysRoleMenuService;
import com.eu.cloud.system.service.ISysRolePermissionService;
import com.eu.cloud.system.service.ISysRoleService;
import com.eu.cloud.system.service.ISysUserRoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private ISysRoleMenuService iSysRoleMenuService;
    private ISysUserRoleService iSysUserRoleService;
    private ISysRolePermissionService iSysRolePermissionService;


    @Override
    public boolean delete(List<Long> ids) {
        Optional.ofNullable(ids).orElse(new ArrayList<>()).forEach(id -> {
            int count = iSysUserRoleService.count(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
            if (count > 0) {
                throw new GlobalException(GlobalExceptionCode.ERROR, "该角色已分配用户，无法删除");
            }
            iSysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
            iSysRolePermissionService.remove(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
        });
        return this.removeByIds(ids);
    }

}
