package com.ryz2593.rpc.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ryz2593
 */
@Data
public class User implements Serializable {
    private Integer id;
    private String name;
    private String sex;
}
