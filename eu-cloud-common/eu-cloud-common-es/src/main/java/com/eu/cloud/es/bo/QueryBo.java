package com.eu.cloud.es.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 条件查询
 *
 * @author jiangxd
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryBo {

    private String key;

    private Object value;

}
