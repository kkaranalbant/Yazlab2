/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kaan.smarteventplanningplatform.repo;

import com.kaan.smarteventplanningplatform.model.Message;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kaan
 */
@Repository
public interface MessageRepo extends JpaRepository <Message , Long>{
    
    public Optional <Message> findByIdAndUserId (Long id , Long userId) ;
    
    public List<Message> findAllByUserId (Long userId) ;
    
    public List<Message> findAllByEventId (Long eventId) ;
    
    public Optional<Message> findByEventIdAndUserId (Long eventId , Long userId) ;
    
    public void deleteByUserId (Long userId) ;
    
}
