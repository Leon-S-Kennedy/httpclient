package com.libowen.model;

import lombok.Data;

import java.security.PrivateKey;
import java.util.Map;

/**
 * @Author: libw1
 * @Date: 2024/04/02/16:51
 * @Description:
 */
@Data
public class RequestFile {
    private String method;
    private Map<String,String> header;
    private String url;
    private Map<String,String> body;
}
