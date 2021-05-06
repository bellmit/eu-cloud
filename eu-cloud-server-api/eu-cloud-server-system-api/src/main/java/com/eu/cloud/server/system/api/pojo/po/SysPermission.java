package com.eu.cloud.server.system.api.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.eu.cloud.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SysPermission extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String perm;
    private Long moduleId;
    private String method;
    private Integer type;

    // 拥有资源权限角色ID集合
    @TableField(exist = false)
    private List<Long> roleIds;
}
