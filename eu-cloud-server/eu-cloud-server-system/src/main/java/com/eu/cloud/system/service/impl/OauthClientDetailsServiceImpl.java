package com.eu.cloud.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eu.cloud.server.system.api.pojo.po.OauthClientDetails;
import com.eu.cloud.system.mapper.OauthClientDetailsMapper;
import com.eu.cloud.system.service.IOauthClientDetailsService;
import org.springframework.stereotype.Service;

/**
 * @author haoxr
 * @date 2020-11-06
 */
@Service
public class OauthClientDetailsServiceImpl extends ServiceImpl<OauthClientDetailsMapper, OauthClientDetails> implements IOauthClientDetailsService {
}
