/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.controller;

import com.kaan.smarteventplanningplatform.dto.request.event.EventCreatingRequest;
import com.kaan.smarteventplanningplatform.dto.request.event.EventIdRequest;
import com.kaan.smarteventplanningplatform.dto.request.event.EventUpdatingRequest;
import com.kaan.smarteventplanningplatform.dto.response.event.EventResponse;
import com.kaan.smarteventplanningplatform.service.EventService;
import com.kaan.smarteventplanningplatform.service.JwtService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author kaan
 */
@RestController
@RequestMapping("/v1/event")
public class EventController {

    private EventService eventService;

    private JwtService jwtService;

    public EventController(EventService eventService, JwtService jwtService) {
        this.eventService = eventService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createEvent(@RequestBody EventCreatingRequest eventCreatingRequest, @CookieValue("Authorization") String encodedJwt) {
        eventService.create(eventCreatingRequest, jwtService.getUserIdByNotParsedToken(encodedJwt));
        return ResponseEntity.ok("You Created Event");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteEvent(@RequestParam Long eventId, @CookieValue("Authorization") String encodedJwt) {
        eventService.delete(new EventIdRequest(eventId), jwtService.getUserIdByNotParsedToken(encodedJwt));
        return ResponseEntity.ok("You Deleted Event");
    }

    @DeleteMapping("/delete-exec")
    public ResponseEntity<String> deleteEventForExecutive(@RequestBody EventIdRequest eventIdRequest) {
        eventService.deleteForExecutive(eventIdRequest);
        return ResponseEntity.ok("You Deleted Event");
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmEvent(@RequestBody EventIdRequest eventIdRequest) {
        eventService.confirmEvent(eventIdRequest);
        return ResponseEntity.ok("You Confirmed Event");
    }

    @PostMapping("/decline")
    public ResponseEntity<String> disableEvent(@RequestBody EventIdRequest eventIdRequest) {
        eventService.disableEvent(eventIdRequest);
        return ResponseEntity.ok("You Disabled Event");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateEvent(@RequestBody EventUpdatingRequest eventUpdatingRequest, @CookieValue("Authorization") String encodedJwt) {
        eventService.updateEvent(eventUpdatingRequest, jwtService.getUserIdByNotParsedToken(encodedJwt));
        return ResponseEntity.ok("You Updated Event");
    }

    @PostMapping("/update-exec")
    public ResponseEntity<String> updateEventExecutive(@RequestBody EventUpdatingRequest eventUpdatingRequest, @CookieValue("Authorization") String encodedJwt) {
        eventService.updateEventForExecutives(eventUpdatingRequest);
        return ResponseEntity.ok("You Updated Event");
    }

    @GetMapping("/get")
    public ModelAndView getEvent(@RequestParam Long eventId) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("event", eventService.getByIdForFrontend(new EventIdRequest(eventId)));
        mv.setViewName("event-page");
        return mv;
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<EventResponse>> getEventsByUserId(@CookieValue("Authorization") String encodedJwt) {
        return ResponseEntity.ok(eventService.getByUserId(jwtService.getUserIdByNotParsedToken(encodedJwt)));
    }

    @GetMapping("/main")
    public ModelAndView getMainEventPage() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("events", eventService.getAll());
        mv.setViewName("main-user-panel");
        return mv;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAll());
    }

}
