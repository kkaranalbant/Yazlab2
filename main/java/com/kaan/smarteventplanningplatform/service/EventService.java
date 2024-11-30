/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.service;

import com.kaan.smarteventplanningplatform.dto.request.event.EventCreatingRequest;
import com.kaan.smarteventplanningplatform.dto.request.event.EventIdRequest;
import com.kaan.smarteventplanningplatform.dto.request.event.EventUpdatingRequest;
import com.kaan.smarteventplanningplatform.dto.response.event.EventResponse;
import com.kaan.smarteventplanningplatform.exception.EventException;
import com.kaan.smarteventplanningplatform.model.Event;
import com.kaan.smarteventplanningplatform.model.User;
import com.kaan.smarteventplanningplatform.repo.EventRepo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 *
 * @author kaan
 */
@Service
public class EventService {

    private EventRepo eventRepo;

    private UserService userService;

    private PointService pointService;

    public EventService(EventRepo eventRepo, @Lazy UserService userService, PointService pointService) {
        this.eventRepo = eventRepo;
        this.userService = userService;
        this.pointService = pointService;
    }

    public void create(EventCreatingRequest eventCreatingRequest, Long userId) throws EventException {
        if (eventCreatingRequest.name() == null || eventCreatingRequest.name().isEmpty()) {
            throw new EventException("Name Can Not Be Empty");
        }
        if (eventCreatingRequest.startingTime().isAfter(eventCreatingRequest.finishingTime())
                || eventCreatingRequest.startingTime().isEqual(eventCreatingRequest.finishingTime())) {
            throw new EventException("Invalid Times");
        }
        if (eventCreatingRequest.explanataion().isEmpty() || eventCreatingRequest.explanataion() == null) {
            throw new EventException("Explanation Can Not Be Empty");
        }
        if (eventCreatingRequest.location().isEmpty() || eventCreatingRequest.location() == null) {
            throw new EventException("Location Can Not Be Empty");
        }
        Optional<Event> eventOptional = getForServiceByName(eventCreatingRequest.name());
        if (eventOptional.isPresent()) {
            throw new EventException("You Can Not Pick This Name");
        }
        User user = userService.getUserById(userId);
        Event event = new Event();
        event.setCategory(eventCreatingRequest.category());
        event.setExplanation(eventCreatingRequest.explanataion());
        event.setEditted(false);
        event.setPending(true);
        event.setFinishTime(eventCreatingRequest.finishingTime());
        event.setStartingTime(eventCreatingRequest.startingTime());
        event.setName(eventCreatingRequest.name());
        event.setLocation(eventCreatingRequest.location());
        event.setUser(user);

        eventRepo.save(event);
        pointService.add(userId, 15L);
    }

    /*
    Normal kullanici
     */
    public void delete(EventIdRequest eventDeletingRequest, Long userId) throws EventException {
        Long eventId = eventDeletingRequest.eventId();
        Optional<Event> eventOptional = eventRepo.findByIdAndUserId(eventId, userId);
        if (eventOptional.isPresent()) {
            if (eventOptional.get().getFinishTime().isBefore(LocalDateTime.now())
                    || eventOptional.get().getFinishTime().isEqual(LocalDateTime.now())) {
                throw new EventException("This event cannot be deleted because it happened");
            }
            eventRepo.delete(eventOptional.get());
            pointService.add(userId, -10L);
        } else {
            throw new EventException("You Can Not Do Process On This Event");
        }
    }

    /*
    Yetkililerin silmesi icin
     */
    public void deleteForExecutive(EventIdRequest eventDeletingRequest) throws EventException {
        Long eventId = eventDeletingRequest.eventId();
        Optional<Event> eventOptional = eventRepo.findById(eventId);
        if (eventOptional.isPresent()) {
            eventRepo.delete(eventOptional.get());
        } else {
            throw new EventException("This Event Is No Longer Available");
        }
    }

    public void deleteByUserId(Long userId) {
        List<Event> events = eventRepo.findAllByUserId(userId);
        eventRepo.deleteAll(events);
    }

    public void confirmEvent(EventIdRequest eventConfirmingRequest) throws EventException {
        Long eventId = eventConfirmingRequest.eventId();
        Optional<Event> eventOptional = eventRepo.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (!event.isPending()) {
                throw new EventException("This Event Is Already Confirmed");
            }
            event.setPending(false);
        } else {
            throw new EventException("This Event Is No Longer Available");
        }
    }

    public void disableEvent(EventIdRequest eventDisablingRequest) throws EventException {
        Long eventId = eventDisablingRequest.eventId();
        Optional<Event> eventOptional = eventRepo.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.isPending()) {
                throw new EventException("This Event Is Already Pending");
            }
            event.setPending(true);
        } else {
            throw new EventException("This Event Is No Longer Available");
        }
    }

    /*
    Event Sahibi icin
     */
    public void updateEvent(EventUpdatingRequest eventUpdatingRequest, Long userId) throws EventException {
        if (eventUpdatingRequest.name() == null || eventUpdatingRequest.name().isEmpty()) {
            throw new EventException("Name Can Not Be Empty");
        }
        if (eventUpdatingRequest.startingTime().isAfter(eventUpdatingRequest.finishingTime())
                || eventUpdatingRequest.startingTime().isEqual(eventUpdatingRequest.finishingTime())) {
            throw new EventException("Invalid Times");
        }
        if (eventUpdatingRequest.explanataion().isEmpty() || eventUpdatingRequest.explanataion() == null) {
            throw new EventException("Explanation Can Not Be Empty");
        }
        if (eventUpdatingRequest.location().isEmpty() || eventUpdatingRequest.location() == null) {
            throw new EventException("Location Can Not Be Empty");
        }
        Optional<Event> eventOptional = eventRepo.findByIdAndUserId(eventUpdatingRequest.id(), userId);
        if (eventOptional.isEmpty()) {
            throw new EventException("You Can Not Do Process On This Event");
        }
        Event event = eventOptional.get();
        event.setName(eventUpdatingRequest.name());
        event.setStartingTime(eventUpdatingRequest.startingTime());
        event.setFinishTime(eventUpdatingRequest.finishingTime());
        event.setExplanation(eventUpdatingRequest.explanataion());
        event.setEditted(true);
        event.setPending(true);
        event.setLocation(eventUpdatingRequest.location());
        event.setCategory(eventUpdatingRequest.category());
        eventRepo.save(event);
    }

    /*
    Yetkililerin event uzerinde yapacagi islemler
     */
    public void updateEventForExecutives(EventUpdatingRequest eventUpdatingRequest) throws EventException {
        if (eventUpdatingRequest.name() == null || eventUpdatingRequest.name().isEmpty()) {
            throw new EventException("Name Can Not Be Empty");
        }
        if (eventUpdatingRequest.startingTime().isAfter(eventUpdatingRequest.finishingTime())
                || eventUpdatingRequest.startingTime().isEqual(eventUpdatingRequest.finishingTime())) {
            throw new EventException("Invalid Times");
        }
        if (eventUpdatingRequest.explanataion().isEmpty() || eventUpdatingRequest.explanataion() == null) {
            throw new EventException("Explanation Can Not Be Empty");
        }
        if (eventUpdatingRequest.location().isEmpty() || eventUpdatingRequest.location() == null) {
            throw new EventException("Location Can Not Be Empty");
        }
        Optional<Event> eventOptional = eventRepo.findById(eventUpdatingRequest.id());
        if (eventOptional.isEmpty()) {
            throw new EventException("This Event Is No Longer Available");
        }
        Event event = eventOptional.get();
        event.setName(eventUpdatingRequest.name());
        event.setStartingTime(eventUpdatingRequest.startingTime());
        event.setFinishTime(eventUpdatingRequest.finishingTime());
        event.setExplanation(eventUpdatingRequest.explanataion());
        event.setEditted(true);
        event.setPending(false);
        event.setLocation(eventUpdatingRequest.location());
        event.setCategory(eventUpdatingRequest.category());
        eventRepo.save(event);
    }

    private Optional<Event> getForServiceByName(String eventName) throws EventException {
        return eventRepo.findByName(eventName);
    }

    public EventResponse getByIdForFrontend(EventIdRequest eventGettingRequest) throws EventException {
        Optional<Event> eventOptional = eventRepo.findById(eventGettingRequest.eventId());
        if (eventOptional.isEmpty()) {
            throw new EventException("This Event Is No Longer Available");
        }
        Event event = eventOptional.get();
        EventResponse eventResponse = new EventResponse(event.getId(), event.getName(), event.getStartingTime(),
                event.getFinishTime(), event.getExplanation(), event.getLocation(), event.getCategory());
        return eventResponse;
    }

    public List<EventResponse> getByUserId(Long userId) {
        List<Event> events = eventRepo.findAllByUserId(userId);
        List<EventResponse> responses = new ArrayList();
        for (Event event : events) {
            EventResponse eventResponse = new EventResponse(event.getId(), event.getName(), event.getStartingTime(),
                    event.getFinishTime(), event.getExplanation(), event.getLocation(), event.getCategory());
            responses.add(eventResponse);
        }
        return responses;
    }

    public List<EventResponse> getAll() {
        List<Event> events = eventRepo.findAll();
        List<EventResponse> responses = new ArrayList();
        for (Event event : events) {
            EventResponse eventResponse = new EventResponse(event.getId(), event.getName(), event.getStartingTime(),
                    event.getFinishTime(), event.getExplanation(), event.getLocation(), event.getCategory());
            responses.add(eventResponse);
        }
        return responses;
    }

    public Event getByIdForServices(Long eventId) throws EventException {
        Optional<Event> eventOptional = eventRepo.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new EventException("This Event Is No Longer Available");
        }
        Event event = eventOptional.get();
        return event;
    }

}
