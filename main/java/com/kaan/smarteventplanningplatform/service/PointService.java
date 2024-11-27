/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.service;

import com.kaan.smarteventplanningplatform.exception.PointException;
import com.kaan.smarteventplanningplatform.model.Point;
import com.kaan.smarteventplanningplatform.repo.PointRepo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author kaan
 */
@Service
public class PointService {

    private PointRepo pointRepo;
    private UserService userService;

    public PointService(PointRepo pointRepo, UserService userService) {
        this.pointRepo = pointRepo;
        this.userService = userService;
    }

    /*
    Sadece normal kullanicilar icin kullanilacak olan metottur ayrica bir 
    endpointten bu metoda erisilmeyecek egerki katilim gibi durumlar olursa baska servisler uzerinden cagrilacak
     */
    public void add(Long userId, Long value) throws PointException {
        Point point = new Point();
        point.setUser(userService.getUserById(userId));
        point.setValue(value);
        point.setEarningTime(LocalDateTime.now());
        pointRepo.save(point);

    }

    /*
    Bir kullanicinin silinmesi ihtimaline karsi
     */
    public void deleteByUserId(Long userId) {
        pointRepo.deleteByUserId(userId);
    }

    public List<Point> getAllByUserId(Long userId) {
        return pointRepo.findAllByUserId(userId);
    }

    public Long getAbsolutePointByUserId(Long userId) {
        Long result = 0L;
        List<Point> points = getAllByUserId(userId);
        for (Point point : points) {
            result += point.getValue();
        }
        return result;
    }

}
