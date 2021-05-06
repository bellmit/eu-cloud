package com.eu.cloud.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eu.cloud.core.exception.GlobalException;
import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import com.eu.cloud.server.system.api.pojo.dto.RolePermissionDTO;
import com.eu.cloud.server.system.api.pojo.po.SysRole;
import com.eu.cloud.system.enums.QueryModeEnum;
import com.eu.cloud.system.service.ISysPermissionService;
import com.eu.cloud.system.service.ISysRoleMenuService;
import com.eu.cloud.system.service.ISysRolePermissionService;
import com.eu.cloud.system.service.ISysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "角色接口")
@RestController
@RequestMapping("/roles")
@Slf4j
@AllArgsConstructor
public class RoleController {

    private ISysRoleService iSysRoleService;

    private ISysRoleMenuService iSysRoleMenuService;

    private ISysRolePermissionService iSysRolePermissionService;

    private ISysPermissionService iSysPermissionService;

    @ApiOperation(value = "列表分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMode", paramType = "query", dataType = "QueryModeEnum"),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "limit", value = "每页数量", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "角色名称", paramType = "query", dataType = "String"),
    })
    @GetMapping
    public GlobalResponseWrapper list(
            String queryMode,
            Integer page,
            Integer limit,
            String name
    ) {
        QueryModeEnum queryModeEnum = QueryModeEnum.getValue(queryMode);
        switch (queryModeEnum) {
            case PAGE:
                LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<SysRole>()
                        .like(StrUtil.isNotBlank(name), SysRole::getName, name)
                        .orderByAsc(SysRole::getSort)
                        .orderByDesc(SysRole::getGmtModified)
                        .orderByDesc(SysRole::getGmtCreate);
                Page<SysRole> result = iSysRoleService.page(new Page<>(page, limit), queryWrapper);
                return new GlobalResponseWrapper().data(result);
            case LIST:
                List list = iSysRoleService.list(new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getStatus, 1));
                return new GlobalResponseWrapper().data(list);
            default:
                throw new GlobalException(GlobalExceptionCode.ERROR, "查询模式类型不存在");
        }
    }


    @ApiOperation(value = "新增角色")
    @ApiImplicitParam(name = "role", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysRole")
    @PostMapping
    public boolean add(@RequestBody SysRole role) {
        boolean result = iSysRoleService.save(role);
        if (result) {
            iSysPermissionService.refreshPermissionRolesCache();
        }
        return result;
    }

    @ApiOperation(value = "修改角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "role", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysRole")
    })
    @PutMapping(value = "/{id}")
    public boolean update(
            @PathVariable Long id,
            @RequestBody SysRole role) {
        boolean result = iSysRoleService.updateById(role);
        if (result) {
            iSysPermissionService.refreshPermissionRolesCache();
        }
        return result;
    }

    @ApiOperation(value = "删除角色")
    @ApiImplicitParam(name = "ids", value = "以,分割拼接字符串", required = true, dataType = "String")
    @DeleteMapping("/{ids}")
    public boolean delete(@PathVariable String ids) {
        boolean result = iSysRoleService.delete(Arrays.asList(ids.split(",")).stream()
                .map(id -> Long.parseLong(id)).collect(Collectors.toList()));
        if (result) {
            iSysPermissionService.refreshPermissionRolesCache();
        }
        return result;
    }

    @ApiOperation(value = "局部更新角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "role", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysRole")
    })
    @PatchMapping(value = "/{id}")
    public boolean patch(@PathVariable Long id, @RequestBody SysRole role) {
        LambdaUpdateWrapper<SysRole> updateWrapper = new LambdaUpdateWrapper<SysRole>()
                .eq(SysRole::getId, id)
                .set(role.getStatus() != null, SysRole::getStatus, role.getStatus());
        boolean result = iSysRoleService.update(updateWrapper);
        if (result) {
            iSysPermissionService.refreshPermissionRolesCache();
        }
        return result;
    }

    @ApiOperation(value = "角色拥有的菜单ID集合")
    @ApiImplicitParam(name = "id", value = "角色id", required = true, paramType = "path", dataType = "Long")
    @GetMapping("/{id}/menu_ids")
    public List<Long> roleMenuIds(@PathVariable("id") Long roleId) {
        return iSysRoleMenuService.listMenuIds(roleId);
    }

    @ApiOperation(value = "角色拥有的权限ID集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "type", value = "权限类型", paramType = "query", dataType = "Integer"),
    })
    @GetMapping("/{id}/permission_ids")
    public List<Long> rolePermissionIds(@PathVariable("id") Long roleId, @RequestParam Integer type) {
        return iSysRolePermissionService.listPermissionIds(roleId, type);
    }


    @ApiOperation(value = "修改角色菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "role", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysRole")
    })
    @PutMapping(value = "/{id}/menu_ids")
    public boolean updateRoleMenuIds(
            @PathVariable("id") Long roleId,
            @RequestBody SysRole role) {

        List<Long> menuIds = role.getMenuIds();
        return iSysRoleMenuService.update(roleId, menuIds);
    }

    @ApiOperation(value = "修改角色权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "rolePermission", value = "实体JSON对象", required = true, paramType = "body", dataType = "RolePermissionDTO")
    })
    @PutMapping(value = "/{id}/permission_ids")
    public boolean updateRolePermissionIds(
            @PathVariable("id") Long roleId,
            @RequestBody RolePermissionDTO rolePermission) {
        rolePermission.setRoleId(roleId);
        boolean result = iSysRolePermissionService.update(rolePermission);
        if (result) {
            iSysPermissionService.refreshPermissionRolesCache();
        }
        return result;
    }
}
