/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.repo;

import com.kaan.smarteventplanningplatform.model.Participant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kaan
 */
@Repository
public interface ParticipantRepo extends JpaRepository<Participant, Long> {

    public Optional<Participant> findByUserIdAndEventId(Long userId, Long eventId);
    
    public List<Participant> findAllByUserId (Long userId) ;
    
    public List<Participant> findAllByEventId (Long eventId) ;
    
    public void deleteByUserId (Long userId) ;
    

}
