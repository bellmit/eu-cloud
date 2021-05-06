package com.eu.cloud.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eu.cloud.server.system.api.pojo.po.SysDept;
import com.eu.cloud.server.system.api.pojo.vo.DeptVO;
import com.eu.cloud.server.system.api.pojo.vo.TreeVO;

import java.util.List;

public interface ISysDeptService extends IService<SysDept> {

    List<DeptVO> listDeptVO(LambdaQueryWrapper<SysDept> baseQuery);

    List<TreeVO> listTreeVO(LambdaQueryWrapper<SysDept> baseQuery);
}
