package com.delix.deliveryou.spring.service_database;

import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository_database.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService1 {
    @Autowired
    private UserRoleRepository userRoleRepository;

    public UserRole getUser(){ return userRoleRepository.getUserRoleById(1); }
    public UserRole getShipper(){
        return userRoleRepository.getUserRoleById(2);
    }
    public UserRole getAdmin(){return userRoleRepository.getUserRoleById(3); }
}
