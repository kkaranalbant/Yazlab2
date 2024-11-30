/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.service;

import com.kaan.smarteventplanningplatform.dto.request.participant.ParticipantEventIdRequest;
import com.kaan.smarteventplanningplatform.dto.request.participant.ParticipantExecutiveDeletingRequest;
import com.kaan.smarteventplanningplatform.dto.response.participant.ParticipantResponse;
import com.kaan.smarteventplanningplatform.exception.ParticipantException;
import com.kaan.smarteventplanningplatform.model.Event;
import com.kaan.smarteventplanningplatform.model.Participant;
import com.kaan.smarteventplanningplatform.repo.ParticipantRepo;
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
public class ParticipantService {

    private ParticipantRepo participantRepo;

    private EventService eventService;

    private UserService userService;

    private PointService pointService;

    public ParticipantService(ParticipantRepo participantRepo, EventService eventService, @Lazy UserService userService, PointService pointService) {
        this.participantRepo = participantRepo;
        this.eventService = eventService;
        this.userService = userService;
        this.pointService = pointService;
    }

    public void create(ParticipantEventIdRequest participantCreatingRequest, Long userId) throws ParticipantException {
        Long eventId = participantCreatingRequest.eventId();
        Optional<Participant> participantOptional = participantRepo.findByUserIdAndEventId(userId, eventId);
        if (participantOptional.isPresent()) {
            throw new ParticipantException("You Have Already Joined This Event");
        }
        Event event = eventService.getByIdForServices(participantCreatingRequest.eventId());
        if (event.getFinishTime().isBefore(LocalDateTime.now()) || event.getFinishTime().isEqual(LocalDateTime.now())) {
            throw new ParticipantException("You can no longer participate in this event");
        }
        if (event.isPending()) {
            throw new ParticipantException("Admin Didnt Confirm This Event Yet");
        }
        Participant participant = new Participant();
        participant.setEvent(eventService.getByIdForServices(eventId));
        participant.setUser(userService.getUserById(userId));
        participant.setJoiningTime(LocalDateTime.now());
        pointService.add(userId, 10L);
        participantRepo.save(participant);
    }

    public void delete(ParticipantEventIdRequest participantEventIdRequest, Long userId) throws ParticipantException {
        Optional<Participant> participantOptional = participantRepo.findByUserIdAndEventId(userId, participantEventIdRequest.eventId());
        if (participantOptional.isPresent()) {
            Event event = eventService.getByIdForServices(participantEventIdRequest.eventId());
            if (event.getFinishTime().isBefore(LocalDateTime.now()) || event.getFinishTime().isEqual(LocalDateTime.now())) {
                throw new ParticipantException("You've participated in this event");
            }
            participantRepo.delete(participantOptional.get());
            pointService.add(userId, -10L);

        } else {
            throw new ParticipantException("You Are Not Remember of This Event");
        }
    }

    public void deleteForExecutives(ParticipantExecutiveDeletingRequest participantExecutiveDeletingRequest) throws ParticipantException {
        Optional<Participant> participantOptional = participantRepo
                .findByUserIdAndEventId(participantExecutiveDeletingRequest.eventId(),
                        participantExecutiveDeletingRequest.userId());
        if (participantOptional.isEmpty()) {
            throw new ParticipantException("This Person is not Remember of This Event");
        }
        participantRepo.delete(participantOptional.get());
        pointService.add(participantExecutiveDeletingRequest.userId(), -10L);
    }

    public void deleteByUserId(Long userId) {
        participantRepo.deleteByUserId(userId);
    }

    public List<ParticipantResponse> getAllByUserId(Long userId) {
        List<Participant> participants = participantRepo.findAllByUserId(userId);
        List<ParticipantResponse> responses = new ArrayList();
        for (Participant participant : participants) {
            ParticipantResponse participantResponse = new ParticipantResponse(participant.getId(), participant.getUser().getId(), participant.getEvent().getId(), participant.getJoiningTime());
            responses.add(participantResponse);
        }
        return responses;
    }

    public List<ParticipantResponse> getAllByEventId(ParticipantEventIdRequest participantEventIdRequest) throws ParticipantException {
        List<Participant> participants = participantRepo.findAllByEventId(participantEventIdRequest.eventId());
        List<ParticipantResponse> responses = new ArrayList();
        for (Participant participant : participants) {
            ParticipantResponse participantResponse = new ParticipantResponse(participant.getId(), participant.getUser().getId(), participant.getEvent().getId(), participant.getJoiningTime());
            responses.add(participantResponse);
        }
        return responses;
    }

    public boolean isParticipant(Long eventId, Long userId) {
        return participantRepo.findByUserIdAndEventId(userId, eventId).isPresent();
    }

}
