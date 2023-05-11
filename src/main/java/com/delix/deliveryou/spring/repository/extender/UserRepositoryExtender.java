package com.delix.deliveryou.spring.repository.extender;

import com.delix.deliveryou.spring.model.SearchFilter;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;

import java.util.List;

public interface UserRepositoryExtender {
    List<User> getUsersWithFilter(UserRole role, SearchFilter filter);
}
