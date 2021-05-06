package com.eu.cloud.auth.service;

import com.eu.cloud.auth.pojo.User;
import com.eu.cloud.core.constants.AuthConstants;
import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import com.eu.cloud.server.system.api.client.UserFeignClient;
import com.eu.cloud.server.system.api.pojo.dto.UserDTO;
import com.eu.cloud.server.system.api.pojo.po.SysUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户认证和授权
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserFeignClient userFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        String clientId = RequestUtils.getAuthClientId();
        SysUser context = (SysUser) SecurityContextHolder.getContext();
        User user = null;
        GlobalResponseWrapper result;
//        switch (clientId) {
//            case AuthConstants.ADMIN_CLIENT_ID: // 后台用户
                result = userFeignClient.getUserByUsername(username);
                log.info("获取用户信息：{}", result.toString());
                if (GlobalExceptionCode.SUCCESS.getCode() == result.getCode()) {
                    UserDTO userDTO = (UserDTO) result.getData();
                    user = new User(userDTO);
                }
//                break;
//        }
        if (user == null || user.getId() == null) {
            throw new UsernameNotFoundException(GlobalExceptionCode.USER_NOT_FOUNT.getMsg());
        } else if (!user.isEnabled()) {
            throw new DisabledException("该账户已被禁用!");
        } else if (!user.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
        return user;
    }

}
