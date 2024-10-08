package com.iteng.startup.model.dto.spark;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExeDto implements Serializable {

    private static final long serialVersionUID = 6421776038448492020L;

    private String sql;

    private String databaseName;
}
