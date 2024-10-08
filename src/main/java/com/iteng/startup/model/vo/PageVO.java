package com.iteng.startup.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author iteng
 * @date 2024-01-03 20:59
 */
@Data
public class PageVO<T> implements Serializable {

    private static final long serialVersionUID = -4431292272476048075L;

    private Long pageNum;

    private Long pageSize;

    private Long total;

    private List<T> records;
}
