package com.cgc.util;

import com.cgc.entity.User;
import org.springframework.stereotype.Component;

/**
 * 使用ThreadLocal实现数据和线程之间的关联存储
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<User>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
