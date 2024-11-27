/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.dto.response.message;

import java.time.LocalDateTime;

/**
 *
 * @author kaan
 */
public record MessageResponse (Long id , Long eventId , Long userId , LocalDateTime sendingTime , String context) {
    
}
