package com.ryz2593.rpc.service;

import com.ryz2593.rpc.api.UserService;
import com.ryz2593.rpc.domain.User;
import org.ryz2593.rpc.server.core.GerryService;

/**
 * @author ryz2593
 */
@GerryService
public class UserServiceImpl implements UserService {

    @Override
    public String addUser(User user) {
        return "编号：" + user.getId() + ",姓名：" + user.getName() + ",性别：" + user.getSex();
    }

    public static void main(String[] args) {
        String path = UserServiceImpl.class.getClassLoader().getResource("").getPath();
        System.out.println(path);
    }
}
