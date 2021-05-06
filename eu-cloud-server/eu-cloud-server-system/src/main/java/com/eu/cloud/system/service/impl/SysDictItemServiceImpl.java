package com.eu.cloud.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eu.cloud.server.system.api.pojo.po.SysDictItem;
import com.eu.cloud.system.mapper.SysDictItemMapper;
import com.eu.cloud.system.service.ISysDictItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Override
    public IPage<SysDictItem> list(Page<SysDictItem> page, SysDictItem dict) {
        List<SysDictItem> list = this.baseMapper.list(page,dict);
        page.setRecords(list);
        return page;
    }

}
