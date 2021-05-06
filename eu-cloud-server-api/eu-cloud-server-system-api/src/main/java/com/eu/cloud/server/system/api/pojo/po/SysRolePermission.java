package com.eu.cloud.server.system.api.pojo.po;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SysRolePermission {
    private Long roleId;
    private Long permissionId;
}
