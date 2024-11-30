/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.dto.response.participant;

import java.time.LocalDateTime;

/**
 *
 * @author kaan
 */
public record ParticipantResponse (Long id ,Long userId , Long eventId , LocalDateTime joiningTime) {
    
}
