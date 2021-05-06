package com.eu.cloud.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eu.cloud.core.exception.GlobalException;
import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import com.eu.cloud.server.system.api.pojo.po.SysDict;
import com.eu.cloud.server.system.api.pojo.po.SysDictItem;
import com.eu.cloud.system.enums.QueryModeEnum;
import com.eu.cloud.system.service.ISysDictItemService;
import com.eu.cloud.system.service.ISysDictService;
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

@Api(tags = "字典接口")
@RestController
@RequestMapping("/dicts")
@AllArgsConstructor
@Slf4j
public class DictController {

    private ISysDictService iSysDictService;

    private ISysDictItemService iSysDictItemService;

    @ApiOperation(value = "列表分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMode", paramType = "query", dataType = "QueryModeEnum"),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页数量", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "字典名称", paramType = "query", dataType = "String"),
    })
    @GetMapping
    public GlobalResponseWrapper list(
            String queryMode,
            Integer page,
            Integer limit,
            String name) {
        QueryModeEnum queryModeEnum = QueryModeEnum.getValue(queryMode);
        LambdaQueryWrapper<SysDict> queryWrapper = new LambdaQueryWrapper<SysDict>()
                .like(StrUtil.isNotBlank(name), SysDict::getName, StrUtil.trimToNull(name))
                .orderByDesc(SysDict::getGmtModified)
                .orderByDesc(SysDict::getGmtCreate);
        switch (queryModeEnum) {
            case PAGE:
                Page<SysDict> result = iSysDictService.page(new Page<>(page, limit), queryWrapper);
                return new GlobalResponseWrapper().data(result);
            default:
                List<SysDict> list = iSysDictService.list(queryWrapper);
                return new GlobalResponseWrapper().data(list);
        }
    }

    @ApiOperation(value = "字典详情")
    @ApiImplicitParam(name = "id", value = "字典id", required = true, paramType = "path", dataType = "Long")
    @GetMapping("/{id}")
    public SysDict detail(@PathVariable Integer id) {
        return iSysDictService.getById(id);
    }

    @ApiOperation(value = "新增字典")
    @ApiImplicitParam(name = "dictItem", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysDictItem")
    @PostMapping
    public boolean add(@RequestBody SysDict dict) {
        return iSysDictService.save(dict);
    }

    @ApiOperation(value = "修改字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字典id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "dictItem", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysDictItem")
    })
    @PutMapping(value = "/{id}")
    public boolean update(
            @PathVariable Long id,
            @RequestBody SysDict dict) {

        boolean status = iSysDictService.updateById(dict);
        if (status) {
            SysDict dbDict = iSysDictService.getById(id);
            // 字典code更新，同步更新字典项code
            if (!StrUtil.equals(dbDict.getCode(), dict.getCode())) {
                iSysDictItemService.update(new LambdaUpdateWrapper<SysDictItem>().eq(SysDictItem::getDictCode, dbDict.getCode())
                        .set(SysDictItem::getDictCode, dict.getCode()));
            }
        }
        return status;
    }

    @ApiOperation(value = "删除字典")
    @ApiImplicitParam(name = "ids", value = "以,分割拼接字符串", required = true, paramType = "query", dataType = "String")
    @DeleteMapping("/{ids}")
    public boolean delete(@PathVariable String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        List<String> codeList = iSysDictService.listByIds(idList).stream().map(item -> item.getCode()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(codeList)) {
            int count = iSysDictItemService.count(new LambdaQueryWrapper<SysDictItem>().in(SysDictItem::getDictCode, codeList));
            if (count > 0) {
                throw new GlobalException(GlobalExceptionCode.ERROR, "删除字典失败，请先删除关联字典数据");
            }
        }
        return iSysDictService.removeByIds(idList);
    }

    @ApiOperation(value = "局部更新字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "dictItem", value = "实体JSON对象", required = true, paramType = "body", dataType = "SysDictItem")
    })
    @PatchMapping(value = "/{id}")
    public boolean patch(@PathVariable Long id, @RequestBody SysDict dict) {
        LambdaUpdateWrapper<SysDict> updateWrapper = new LambdaUpdateWrapper<SysDict>().eq(SysDict::getId, id);
        updateWrapper.set(dict.getStatus() != null, SysDict::getStatus, dict.getStatus());
        return iSysDictService.update(updateWrapper);
    }
}
