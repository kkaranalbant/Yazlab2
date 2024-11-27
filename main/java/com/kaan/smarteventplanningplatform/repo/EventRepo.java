/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.repo;

import com.kaan.smarteventplanningplatform.model.Event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kaan
 */
@Repository
public interface EventRepo extends JpaRepository<Event, Long> {

    public Optional<Event> findByName(String name);
    
    public Optional<Event> findByIdAndUserId (Long id , Long userId) ;
    
    public List<Event> findAllByUserId (Long userId) ;
    
}
