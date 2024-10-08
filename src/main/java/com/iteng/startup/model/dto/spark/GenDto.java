package com.iteng.startup.model.dto.spark;

import com.iteng.startup.model.entity.TableInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GenDto implements Serializable {
    private static final long serialVersionUID = 6399231143582747980L;
    private String message;
    private List<TableInfo> lists;
}
