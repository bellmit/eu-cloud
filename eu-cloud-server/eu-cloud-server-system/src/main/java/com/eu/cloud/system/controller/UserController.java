package com.eu.cloud.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eu.cloud.core.exception.GlobalException;
import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import com.eu.cloud.server.system.api.pojo.dto.UserDTO;
import com.eu.cloud.server.system.api.pojo.po.SysUser;
import com.eu.cloud.server.system.api.pojo.po.SysUserRole;
import com.eu.cloud.system.service.ISysPermissionService;
import com.eu.cloud.system.service.ISysUserRoleService;
import com.eu.cloud.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "用户接口")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private ISysUserRoleService iSysUserRoleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ISysPermissionService iSysPermissionService;


    @ApiOperation(value = "列表分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "limit", value = "每页数量", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "nickname", value = "用户昵称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "手机号码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "deptId", value = "部门ID", paramType = "query", dataType = "Long"),
    })
    @GetMapping
    public IPage<SysUser> list(
            Integer page,
            Integer limit,
            String nickname,
            String mobile,
            Integer status,
            Long deptId
    ) {

        SysUser user = new SysUser();
        user.setNickname(nickname);
        user.setMobile(mobile);
        user.setStatus(status);
        user.setDeptId(deptId);

        return iSysUserService.list(new Page<>(page, limit), user);
    }

    @ApiOperation(value = "用户详情")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path", dataType = "Long")
    @GetMapping("/{id}")
    public SysUser detail(@PathVariable Long id) {
        SysUser user = iSysUserService.getById(id);
        if (user != null) {
            List<Long> roleIds = iSysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                    .eq(SysUserRole::getUserId, user.getId())
                    .select(SysUserRole::getRoleId)
            ).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            user.setRoleIds(roleIds);
        }
        return user;
    }

    @ApiOperation(value = "新增用户")
    @ApiImplicitParam(name = "user", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysUser")
    @PostMapping
    public boolean add(@RequestBody SysUser user) {
        return iSysUserService.saveUser(user);
    }

    @ApiOperation(value = "修改用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "user", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysUser")
    })
    @PutMapping(value = "/{id}")
    public boolean update(
            @PathVariable Integer id,
            @RequestBody SysUser user) {
        return iSysUserService.updateUser(user);
    }

    @ApiOperation(value = "删除用户")
    @ApiImplicitParam(name = "ids", value = "id集合", required = true, paramType = "query", dataType = "String")
    @DeleteMapping("/{ids}")
    public boolean delete(@PathVariable String ids) {
        return iSysUserService.removeByIds(Arrays.asList(ids.split(",")).stream().map(id -> Long.parseLong(id)).collect(Collectors.toList()));
    }

    @ApiOperation(value = "局部更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "user", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysUser")
    })
    @PatchMapping(value = "/{id}")
    public boolean patch(@PathVariable Long id, @RequestBody SysUser user) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, id);
        updateWrapper.set(user.getStatus() != null, SysUser::getStatus, user.getStatus());
        updateWrapper.set(user.getPassword() != null, SysUser::getPassword, passwordEncoder.encode(user.getPassword()));
        return iSysUserService.update(updateWrapper);
    }


    /**
     * 提供用于用户登录认证需要的用户信息
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "根据用户名获取用户信息")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "path", dataType = "String")
    @GetMapping("/username/{username}")
    public GlobalResponseWrapper getUserByUsername(@PathVariable String username) {
        SysUser user = iSysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));

        // 用户不存在，返回自定义异常，让调用端处理后续逻辑
        if (user == null) {
            throw new GlobalException(GlobalExceptionCode.ERROR, "用户不存在");
        }

        // Entity->DTO
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(user, userDTO);

        // 获取用户的角色ID集合
        List<Long> roleIds = iSysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getId())
        ).stream().map(item -> item.getRoleId()).collect(Collectors.toList());
        userDTO.setRoleIds(roleIds);

        return new GlobalResponseWrapper().data(userDTO);
    }


//    @ApiOperation(value = "获取当前登陆的用户信息")
//    @SentinelResource(value = "getCurrentUser",
//            blockHandlerClass = UserBlockHandler.class, blockHandler = "handleGetCurrentUserBlock"
//    )
//    @GetMapping("/me")
//    public UserVO getCurrentUser() {
//        log.info("获取当前登陆的用户信息 begin");
//
//        UserVO userVO = new UserVO();
//
//        // 用户基本信息
//        Long userId = RequestUtils.getUserId();
//        SysUser user = iSysUserService.getById(userId);
//        BeanUtil.copyProperties(user, userVO);
//
//        // 用户角色信息
//        List<Long> roleIds = RequestUtils.getRoleIds();
//        userVO.setRoles(roleIds);
//
//        // 用户按钮权限信息
//        List<String> perms = iSysPermissionService.listPermsByRoleIds(roleIds, PermTypeEnum.BUTTON.getValue());
//        userVO.setPerms(perms);
//
//        return userVO;
//    }
}
