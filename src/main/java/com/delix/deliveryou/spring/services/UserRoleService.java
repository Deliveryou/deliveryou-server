package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    public UserRole getUser(){ return userRoleRepository.getUserRoleById(1); }
    public UserRole getShipper(){
        return userRoleRepository.getUserRoleById(2);
    }
    public UserRole getAdmin(){return userRoleRepository.getUserRoleById(3); }
}
