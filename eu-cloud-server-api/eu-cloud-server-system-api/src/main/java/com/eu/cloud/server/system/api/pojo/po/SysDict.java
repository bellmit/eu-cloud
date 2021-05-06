package com.eu.cloud.server.system.api.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.eu.cloud.mybatis.base.BaseEntity;
import lombok.Data;

@Data
public class SysDict extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private Integer status;

    private  String  remark;

}
