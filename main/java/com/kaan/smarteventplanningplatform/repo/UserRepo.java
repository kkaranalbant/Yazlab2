/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kaan.smarteventplanningplatform.repo;

import com.kaan.smarteventplanningplatform.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author kaan
 */
public interface UserRepo extends JpaRepository<User, Long> {

    public Optional<User> findByUsername(String username);
    
    public Optional<User> findByEmail (String email) ;

}
