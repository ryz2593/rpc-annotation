package com.ryz2593.rpc.api;

import com.ryz2593.rpc.domain.User;

/**
 * @author ryz2593
 */
public interface UserService {
    /**
     * add user
     * @param user
     * @return
     */
    String addUser(User user);

}
