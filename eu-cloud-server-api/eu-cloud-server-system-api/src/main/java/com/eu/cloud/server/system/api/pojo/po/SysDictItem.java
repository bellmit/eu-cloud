package com.eu.cloud.server.system.api.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.eu.cloud.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SysDictItem extends BaseEntity {

    @TableId(type= IdType.AUTO)
    private Long id;

    private String name;

    private String value;

    private String dictCode;

    private String sort;

    private Integer status;

    private Integer defaulted;

    private String remark;

}
