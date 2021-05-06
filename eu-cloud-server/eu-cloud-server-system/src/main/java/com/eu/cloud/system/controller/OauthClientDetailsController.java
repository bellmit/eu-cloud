package com.eu.cloud.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eu.cloud.server.system.api.pojo.po.OauthClientDetails;
import com.eu.cloud.system.service.IOauthClientDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Api(tags = "客户端接口")
@RestController
@RequestMapping("/clients")
@Slf4j
@AllArgsConstructor
public class OauthClientDetailsController {

    private IOauthClientDetailsService iOauthClientDetailsService;

    @ApiOperation(value = "列表分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "limit", value = "每页数量", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "clientId", value = "客户端ID", paramType = "query", dataType = "String")
    })
    @GetMapping
    public IPage list(Integer page,
                      Integer limit,
                      String clientId) {
        IPage<OauthClientDetails> result = iOauthClientDetailsService.page(
                new Page<>(page, limit),
                new LambdaQueryWrapper<OauthClientDetails>()
                        .like(StrUtil.isNotBlank(clientId), OauthClientDetails::getClientId, clientId));
        return result;
    }

    @ApiOperation(value = "客户端详情")
    @ApiImplicitParam(name = "clientId", value = "客户端id", required = true, paramType = "path", dataType = "String")
    @GetMapping("/{clientId}")
    public OauthClientDetails detail(@PathVariable String clientId) {
        return iOauthClientDetailsService.getById(clientId);
    }

    @ApiOperation(value = "新增客户端")
    @ApiImplicitParam(name = "client", value = "实体JSON对象", required = true, paramType = "body", dataType = "OauthClientDetails")
    @PostMapping
    public boolean add(@RequestBody OauthClientDetails client) {
        return iOauthClientDetailsService.save(client);
    }

    @ApiOperation(value = "修改客户端")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "客户端id", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "client", value = "实体JSON对象", required = true, paramType = "body", dataType = "OauthClientDetails")
    })
    @PutMapping(value = "/{clientId}")
    public boolean update(
            @PathVariable String clientId,
            @RequestBody OauthClientDetails client) {
        return iOauthClientDetailsService.updateById(client);
    }

    @ApiOperation(value = "删除客户端")
    @ApiImplicitParam(name = "ids", value = "id集合,以,拼接字符串", required = true, paramType = "query", dataType = "String")
    @DeleteMapping("/{ids}")
    public boolean delete(@PathVariable("ids") String ids) {
        return iOauthClientDetailsService.removeByIds(Arrays.asList(ids.split(",")));
    }
}
