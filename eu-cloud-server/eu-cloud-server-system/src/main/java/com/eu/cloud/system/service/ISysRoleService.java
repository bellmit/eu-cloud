package com.eu.cloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eu.cloud.server.system.api.pojo.po.SysRole;

import java.util.List;

public interface ISysRoleService extends IService<SysRole> {

    boolean delete(List<Long> ids);
}
