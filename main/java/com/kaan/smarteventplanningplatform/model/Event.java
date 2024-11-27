/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author kaan
 */
@Entity
@Table(name="events")
@Data
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    
    @Column(unique = true)
    private String name ;
    
    private LocalDateTime startingTime ;
    
    private LocalDateTime finishTime ;
    
    private String explanation ; 
    
    private String location ;
    
    private Category category ; 
    
    @JoinColumn(name = "user_id")
    private User user ;
    
    private boolean isPending ;
    
    private boolean isEditted ; 

    public Event(String name, LocalDateTime startingTime, LocalDateTime finishTime, String explanation, String location, Category category, User user, boolean isPending, boolean isEditted) {
        this.name = name;
        this.startingTime = startingTime;
        this.finishTime = finishTime;
        this.explanation = explanation;
        this.location = location;
        this.category = category;
        this.user = user;
        this.isPending = isPending;
        this.isEditted = isEditted;
    }
    
    
    
}
