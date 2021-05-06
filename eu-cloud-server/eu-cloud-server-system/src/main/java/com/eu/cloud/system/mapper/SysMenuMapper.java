package com.eu.cloud.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eu.cloud.server.system.api.pojo.po.SysMenu;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author haoxr
 * @date 2020-11-06
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("<script>" +
            "   select id,name,parent_id,path,component,icon,sort,visible,redirect from sys_menu " +
            "   where visible=1" +
            "   order by sort asc" +
            "</script>")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            // 一对多关联查询拥有菜单访问权限的角色ID集合
            @Result(property = "roles", column = "id", many = @Many(select = "com.eu.cloud.system.mapper.SysRoleMenuMapper.listByMenuId"))
    })
    List<SysMenu> listForRouter();
}
