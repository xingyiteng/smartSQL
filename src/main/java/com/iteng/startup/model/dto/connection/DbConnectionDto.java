package com.iteng.startup.model.dto.connection;

import lombok.Data;

import java.io.Serializable;

@Data
public class DbConnectionDto implements Serializable {
    private static final long serialVersionUID = 4955688942792320736L;
    private String host;
    private int port;
    private String username;
    private String password;
}