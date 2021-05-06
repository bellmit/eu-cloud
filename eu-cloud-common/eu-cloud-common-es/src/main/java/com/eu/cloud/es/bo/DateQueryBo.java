package com.eu.cloud.es.bo;

import lombok.Data;

/**
 * 日期查询
 *
 * @author jiangxd
 */
@Data
public class DateQueryBo {

    private String field;

    private String startTime;

    private String endTime;
}
