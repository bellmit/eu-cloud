package com.eu.cloud.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eu.cloud.server.system.api.pojo.po.OauthClientDetails;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OauthClientDetailsMapper  extends BaseMapper<OauthClientDetails> {
}
