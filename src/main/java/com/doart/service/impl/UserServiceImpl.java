package com.doart.service.impl;

import com.doart.dao.UserDao;
import com.doart.bean.User;
import com.doart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public List<User> findAllUser() {

        List<User> result = userDao.findAllUser();
        return result;
    }
}
