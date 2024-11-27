/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kaan.smarteventplanningplatform.repo;

import com.kaan.smarteventplanningplatform.model.Point;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kaan
 */
@Repository
public interface PointRepo extends JpaRepository<Point, Long> {
    
    public Optional<Point> findByUserId (Long userId) ;
    
    public void deleteByUserId (Long userId) ; 
    
    public List<Point> findAllByUserId (Long userId) ;
    
}
