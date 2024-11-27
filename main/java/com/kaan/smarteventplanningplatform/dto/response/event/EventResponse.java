/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.dto.response.event;

import com.kaan.smarteventplanningplatform.model.Category;
import java.time.LocalDateTime;

/**
 *
 * @author kaan
 */
public record EventResponse(Long id , String name, LocalDateTime startingTime,
         LocalDateTime finishingTime, String explanataion, String location,
         Category category) {

}
