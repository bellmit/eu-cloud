package com.eu.cloud.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eu.cloud.server.system.api.pojo.po.SysDict;
import com.eu.cloud.system.mapper.SysDictMapper;
import com.eu.cloud.system.service.ISysDictService;
import org.springframework.stereotype.Service;

@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

}
