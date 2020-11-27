package com.doart.dao;

import com.doart.bean.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    List<User> findAllUser();
}
