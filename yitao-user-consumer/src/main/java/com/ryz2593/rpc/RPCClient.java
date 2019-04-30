package com.ryz2593.rpc;

import com.ryz2593.rpc.entity.RequestData;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author ryz2593
 */
public class RPCClient {
    public static <T> T getRemoteProxy(final Class<T> interfaceClass, final InetSocketAddress address) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        try (Socket client = new Socket()) {
                            client.connect(address);
                            try (ObjectOutputStream serializer = new ObjectOutputStream(client.getOutputStream());
                                 ObjectInputStream deSerializer = new ObjectInputStream(client.getInputStream())
                            ) {
                                //序列化请求对象
                                RequestData requestData = new RequestData();
                                requestData.setInterfaceName(interfaceClass.getName());
                                requestData.setMethodName(method.getName());
                                requestData.setParameterTypes(method.getParameterTypes());
                                requestData.setParameters(args);

                                serializer.writeObject(requestData);
                                //把调用服务端序列化结果反序列化出来
                                return deSerializer.readObject();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
    }
}
