package com.ryz2593.rpc;

import com.ryz2593.rpc.api.UserService;
import com.ryz2593.rpc.domain.User;

import java.net.InetSocketAddress;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 12345);
        UserService userService = RPCClient.getRemoteProxy(UserService.class, address);

        User user = new User();
        user.setId(123);
        user.setName("张三");
        user.setSex("男");
        String result = userService.addUser(user);
        System.out.println(result);
    }
}
