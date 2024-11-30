/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.service;

import com.kaan.smarteventplanningplatform.dto.request.message.MessageIdRequest;
import com.kaan.smarteventplanningplatform.dto.request.message.MessageSendingRequest;
import com.kaan.smarteventplanningplatform.dto.request.message.MessageUpdatingRequest;
import com.kaan.smarteventplanningplatform.dto.response.message.MessageResponse;
import com.kaan.smarteventplanningplatform.exception.MessageException;
import com.kaan.smarteventplanningplatform.model.Event;
import com.kaan.smarteventplanningplatform.model.Message;
import com.kaan.smarteventplanningplatform.model.User;
import com.kaan.smarteventplanningplatform.repo.MessageRepo;
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
public class MessageService {

    private MessageRepo messageRepo;
    private EventService eventService;
    private UserService userService;

    public MessageService(MessageRepo messageRepo, EventService eventService, @Lazy UserService userService) {
        this.messageRepo = messageRepo;
        this.eventService = eventService;
        this.userService = userService;
    }

    public void add(MessageSendingRequest messageSendingRequest, Long userId) throws MessageException {
        String explanation = messageSendingRequest.explanation();
        if (explanation == null || explanation.isEmpty()) {
            throw new MessageException("Message can not be Empty");
        }
        Long eventId = messageSendingRequest.eventId();
        Message message = new Message();
        message.setContext(explanation);
        Event event = eventService.getByIdForServices(eventId);
        User user = userService.getUserById(userId);
        message.setEvent(event);
        message.setUser(user);
        message.setSendingTime(LocalDateTime.now());
        message.setEditted(false);
        messageRepo.save(message);
    }

    /*
    Normal kullanicinin mesajini silmesi icin ayrica executiveler icin de bu endpoint kullanilacak
     */
    public void delete(MessageIdRequest messageIdRequest, Long userId) throws MessageException {
        Long messageId = messageIdRequest.id();
        if (messageRepo.findById(messageId).isEmpty()) {
            throw new MessageException("Message Not Found");
        }
        Optional<Message> messageOptional = messageRepo.findByIdAndUserId(messageId, userId);
        if (messageOptional.isEmpty()) {
            throw new MessageException("You Can't Do Process On This Message");
        }
        messageRepo.delete(messageOptional.get());
    }

    public Message get(Long messageId) throws MessageException {
        return messageRepo.findById(messageId).orElseThrow(() -> new MessageException("Message Not Found"));
    }

    public MessageResponse getForFrontEnd(MessageIdRequest messageIdRequest) throws MessageException {
        Optional<Message> messageOptional = messageRepo.findById(messageIdRequest.id());
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            MessageResponse messageResponse = new MessageResponse(message.getId(),
                    message.getEvent().getId(), message.getUser().getId(),
                    message.getSendingTime(), message.getContext());
            return messageResponse;
        }
        throw new MessageException("Message Not Found");
    }

    public List<MessageResponse> getAllForEvent(Long eventId) {
        List<Message> messages = messageRepo.findAllByEventId(eventId);
        List<MessageResponse> messageResponses = new ArrayList();
        for (Message message : messages) {
            messageResponses.add(convert(message));
        }
        return messageResponses;
    }

    public void deleteByUserId(Long userId) {
        messageRepo.deleteByUserId(userId);
    }

    /*
    frontendden parametre almayip normal kullanicilar icin kullanilacak olan ve parametre alan ve yetkilileri icin kullanilan iki farkli endpointte kullanilacak
     */
    public List<MessageResponse> getAllForUser(Long userId) {
        List<Message> messages = messageRepo.findAllByUserId(userId);
        List<MessageResponse> messageResponses = new ArrayList();
        for (Message message : messages) {
            messageResponses.add(convert(message));
        }
        return messageResponses;
    }

    public void update(MessageUpdatingRequest messageUpdatingRequest, Long userId) throws MessageException {
        if (messageUpdatingRequest.newContext().isEmpty() || messageUpdatingRequest == null) {
            throw new MessageException("Please Enter Valid Explanation");
        }
        Message message = messageRepo.findById(messageUpdatingRequest.commentId())
                .orElseThrow(() -> new MessageException("Updating Error"));
        if (message.getUser().getId().longValue() != userId) {
            throw new MessageException("You Can't Do Process On This Message");
        }
        message.setContext(messageUpdatingRequest.newContext());
        message.setEditted(true);
        message.setSendingTime(LocalDateTime.now());
        messageRepo.save(message);
    }

    private MessageResponse convert(Message message) {
        MessageResponse messageResponse = new MessageResponse(message.getId(),
                message.getEvent().getId(), message.getUser().getId(),
                message.getSendingTime(), message.getContext());
        return messageResponse;
    }

}
