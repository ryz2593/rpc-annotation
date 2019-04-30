package com.ryz2593.rpc.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ryz2593
 */
@Data
public class RequestData implements Serializable {
    /**
     * 服务接口的全名
     */
    private String interfaceName;

    /**
     * 暴露接口中调用 的方法名称
     */
    private String methodName;

    /**
     * 方法参数类型数组
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数值数组
     */
    private Object[] parameters;
}
