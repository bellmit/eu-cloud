package com.eu.cloud.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eu.cloud.server.system.api.pojo.po.SysDictItem;

public interface ISysDictItemService extends IService<SysDictItem> {

    IPage<SysDictItem> list(Page<SysDictItem> page, SysDictItem dict);

}
