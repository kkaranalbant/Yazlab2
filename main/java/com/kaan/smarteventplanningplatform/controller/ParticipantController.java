/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.controller;

import com.kaan.smarteventplanningplatform.dto.request.participant.ParticipantEventIdRequest;
import com.kaan.smarteventplanningplatform.dto.request.participant.ParticipantExecutiveDeletingRequest;
import com.kaan.smarteventplanningplatform.dto.response.participant.ParticipantResponse;
import com.kaan.smarteventplanningplatform.service.JwtService;
import com.kaan.smarteventplanningplatform.service.ParticipantService;
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

/**
 *
 * @author kaan
 */
@RestController
@RequestMapping("/v1/participant")
public class ParticipantController {

    private ParticipantService participantService;

    private JwtService jwtService;

    public ParticipantController(ParticipantService participantService, JwtService jwtService) {
        this.participantService = participantService;
        this.jwtService = jwtService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> create(@RequestBody ParticipantEventIdRequest participantCreatingRequest, @CookieValue("Authorization") String encodedJwt) {
        participantService.create(participantCreatingRequest, jwtService.getUserIdByNotParsedToken(encodedJwt));
        return ResponseEntity.ok("You Joined An Event");
    }

    @DeleteMapping("/leave")
    public ResponseEntity<String> delete(@RequestParam Long participantId, @CookieValue("Authorization") String encodedJwt) {
        participantService.delete(new ParticipantEventIdRequest(participantId), jwtService.getUserIdByNotParsedToken(encodedJwt));
        return ResponseEntity.ok("You left An Event");
    }

    @DeleteMapping("/leave-exec")
    public ResponseEntity<String> deleteForExecutives(@RequestBody ParticipantExecutiveDeletingRequest participantExecutiveDeletingRequest) {
        participantService.deleteForExecutives(participantExecutiveDeletingRequest);
        return ResponseEntity.ok("You Deleted a participation");
    }

    @DeleteMapping("/delete-all-user")
    public ResponseEntity<String> deleteByUserId(@RequestParam Long userId) {
        participantService.deleteByUserId(userId);
        return ResponseEntity.ok("You Deleted a User's All participations");
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ParticipantResponse>> getAllByUserId(@CookieValue("Authorization") String encodedJwt) {
        return ResponseEntity.ok(participantService.getAllByUserId(jwtService.getUserIdByNotParsedToken(encodedJwt)));
    }

    @GetMapping("/get-all-exec")
    public ResponseEntity<List<ParticipantResponse>> getAllByUserIdExecutive(@RequestParam Long userId) {
        return ResponseEntity.ok(participantService.getAllByUserId(userId));
    }

    @GetMapping("/get-all-event")
    public ResponseEntity<List<ParticipantResponse>> getAllByEventId(@RequestBody ParticipantEventIdRequest participantEventIdRequest) {
        return ResponseEntity.ok(participantService.getAllByEventId(participantEventIdRequest));
    }

    @GetMapping("/is-participant")
    public ResponseEntity<Boolean> isParticipant(@RequestParam Long eventId, @CookieValue("Authorization") String encodedJwt) {
        return ResponseEntity.ok(participantService.isParticipant(eventId, jwtService.getUserIdByNotParsedToken(encodedJwt)));
    }

}
