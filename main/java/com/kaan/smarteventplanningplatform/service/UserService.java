/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.service;

import com.kaan.smarteventplanningplatform.dto.request.PasswordUpdatingRequest;
import com.kaan.smarteventplanningplatform.dto.request.PersonAddingRequest;
import com.kaan.smarteventplanningplatform.dto.request.user.PasswordResetControlRequest;
import com.kaan.smarteventplanningplatform.dto.request.user.PasswordResetRequest;
import com.kaan.smarteventplanningplatform.dto.request.user.UserUpdatingRequest;
import com.kaan.smarteventplanningplatform.dto.response.UserResponse;
import com.kaan.smarteventplanningplatform.exception.UserException;
import com.kaan.smarteventplanningplatform.model.Role;
import com.kaan.smarteventplanningplatform.model.User;
import com.kaan.smarteventplanningplatform.repo.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Set;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author kaan
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passEncoder;
    private final PointService pointService;
    private final JwtService jwtService;
    private final ParticipantService participantService;
    private final MessageService messageService;
    private final EventService eventService;
    private final EmailService emailService;
    private final Random random;

    public UserService(UserRepo userRepo, @Lazy BCryptPasswordEncoder passEncoder, PointService pointService, JwtService jwtService, ParticipantService participantService, MessageService messageService, EventService eventService, EmailService emailService, Random random) {
        this.userRepo = userRepo;
        this.passEncoder = passEncoder;
        this.pointService = pointService;
        this.jwtService = jwtService;
        this.participantService = participantService;
        this.messageService = messageService;
        this.eventService = eventService;
        this.emailService = emailService;
        this.random = random;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Invalid Username Or Password"));
    }

    /*
    kullanici goruntuleme (uygulama icerisinden)
     */
    public User getUserById(Long id) throws UserException {
        return userRepo.findById(id).orElseThrow(() -> new UserException("User Not Found"));
    }

    /*
    Kullanici bilgilerini disariya acmak icin kullanilacak olan metot
    Eger ki hesap private ise actor follower ise veriler gonderilir aksi halde exception
    Normal kullanicilar icin tasarlandi
    Kullanici kendi hesabi hakkindaki tum bilgilere erisebilir.
    Ancak diger kullanicilar icin sinirli
     */
    public UserResponse getUserByIdForOutside(Long actorId, Long id) throws UserException {
        User user = getUserById(id);
        UserResponse userResponse = null;
        boolean isSamePerson = actorId.longValue() == id.longValue();
        if (isSamePerson) {
            userResponse = createUserResponseForSelf(user);
            return userResponse;
        }
        userResponse = createUserResponseForOthers(user);
        return userResponse;
    }

    // admin icin
    public UserResponse getUser(Long userId) throws UserException {
        User user = getUserById(userId);
        UserResponse userResponse = createUserResponseForSelf(user);
        return userResponse;
    }

    private UserResponse createUserResponseForSelf(User user) {
        UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getLastname(), user.getEmail(), user.getUsername(), user.getBirthDate(), user.getGender(), user.getLocation(), user.getPhoneNumber(), user.getImage());
        return userResponse;
    }

    private UserResponse createUserResponseForOthers(User user) {
        UserResponse userResponse = new UserResponse(user.getId(), null, null, null, user.getUsername(), null, user.getGender(), null, null, user.getImage());
        return userResponse;
    }

    private User createPerson(PersonAddingRequest personAddingRequest) {
        throwExceptionIfPasswordFieldsDoesntMatch(personAddingRequest.password(), personAddingRequest.rePassword());
        User user = new User();
        user.setName(personAddingRequest.name());
        user.setLastname(personAddingRequest.lastname());
        user.setEmail(personAddingRequest.email());
        user.setUsername(personAddingRequest.username());
        user.setPassword(passEncoder.encode(personAddingRequest.password()));
        user.setBirthDate(personAddingRequest.birthDate());
        user.setGender(personAddingRequest.gender());
        user.setPhoneNumber(personAddingRequest.phoneNumber());
        user.setLocation(personAddingRequest.location());
        user.setImage(personAddingRequest.image());
        user.setVerificationUrl(null);
        user.setIsEnabled(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(false);
        return user;
    }

    private void throwExceptionIfPasswordFieldsDoesntMatch(String pass, String rePass) throws UserException {
        if (!pass.equals(rePass)) {
            throw new UserException("Password Fields Doesn't Match");
        }
    }

    /*
    kullanici kayit olacagi zaman
     */
    public void addUser(PersonAddingRequest userAddingRequest) throws UserException {
        User user = createPerson(userAddingRequest);
        user.setAuthorities(Set.of(Role.ROLE_USER));
        userRepo.save(user);
        pointService.add(userRepo.findByUsername(user.getUsername()).get().getId(), 20L);
    }

    /*
    Kullanicinin kendi sifresini guncellemesi icin
     */
    @Transactional
    public void updatePassword(Long actorId, PasswordUpdatingRequest passwordUpdatingRequest) throws UserException {
        throwExceptionIfPasswordFieldsDoesntMatch(passwordUpdatingRequest.password(), passwordUpdatingRequest.rePassword());
        User user = getUserById(actorId);
        user.setPassword(passEncoder.encode(passwordUpdatingRequest.password()));
        userRepo.save(user);
    }

    /*
    Adminin veya moderatorlerin bir hesabi kitlemesi icin olusturuldu
     */
    @Transactional
    public void lockUserAccount(Long userId) throws UserException {
        User user = getUserById(userId);
        boolean userHasOnlyUserRole = !user.getAuthorities().contains(Role.ROLE_ADMIN);
        if (!userHasOnlyUserRole) {
            throw new UserException("This User Has More Authorities Than The User Role");
        }
        boolean isAccountAlreadyLocked = user.isAccountNonLocked() == false;
        if (isAccountAlreadyLocked) {
            throw new UserException("This Account Is Already Locked");
        }
        user.setIsAccountNonLocked(false);
        userRepo.save(user);
    }

    /*
    Adminin veya moderatorlerin kilidi kaldirmasi icin
     */
    @Transactional
    public void unlockUserAccount(Long userId) {
        User user = getUserById(userId);
        boolean isAccountAlreadyNonLocked = user.isAccountNonLocked() == true;
        if (isAccountAlreadyNonLocked) {
            throw new UserException("This Account Is Already Locked");
        }
        user.setIsAccountNonLocked(true);
        userRepo.save(user);
    }

    @Transactional
    public void update(Long userId, UserUpdatingRequest userUpdatingRequest) throws UserException {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserException("Invalid User Id"));
        if (!user.getUsername().equals(userUpdatingRequest.username()) && userRepo.findByUsername(userUpdatingRequest.username()).isPresent()) {
            throw new UserException("You Cant Use This Username");
        }
        if (!user.getEmail().equals(userUpdatingRequest.email()) && userRepo.findByEmail(userUpdatingRequest.email()).isPresent()) {
            throw new UserException("YOu Cant Use This Email");
        }
        user.setBirthDate(userUpdatingRequest.birthDate());
        user.setEmail(userUpdatingRequest.email());
        user.setGender(userUpdatingRequest.gender());
        user.setImage(userUpdatingRequest.image());
        user.setLastname(userUpdatingRequest.lastname());
        user.setLocation(userUpdatingRequest.location());
        user.setName(userUpdatingRequest.name());
        user.setUsername(userUpdatingRequest.username());
        user.setPhoneNumber(userUpdatingRequest.phoneNumber());
        userRepo.save(user);
    }

    public void deleteAccount(Long userId) throws UserException {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserException("Invalid User Id"));
        pointService.deleteByUserId(userId);
        eventService.deleteByUserId(userId);
        jwtService.deleteByUserId(userId);
        participantService.deleteByUserId(userId);
        messageService.deleteByUserId(userId);
        userRepo.delete(user);
    }

    public void sendPasswordResetLink(PasswordResetControlRequest passwordResetControlRequest) throws UserException {
        User user1 = userRepo.findByUsername(passwordResetControlRequest.username()).orElseThrow(() -> new UserException("This Username is Invalid"));
        User user2 = userRepo.findByEmail(passwordResetControlRequest.email()).orElseThrow(() -> new UserException("This Email is Invalid"));
        if (user1.getId().longValue() != user2.getId().longValue()) {
            throw new UserException("Username and Email doesnt Match");
        }
        StringBuilder verificationUrlSb = new StringBuilder();
        while (verificationUrlSb.length() != 36) {
            verificationUrlSb.append((char) random.nextInt(48, 58));
            verificationUrlSb.append((char) random.nextInt(33, 42));
            verificationUrlSb.append((char) random.nextInt(65, 91));
            verificationUrlSb.append((char) random.nextInt(97, 123));
        }
        user1.setVerificationUrl(verificationUrlSb.toString());
        user1.setIsAccountNonLocked(false);
        try {
            emailService.sendResetMail(passwordResetControlRequest.email(), user1.getName() + " " + user1.getLastname(), "http://localhost:8080/" + verificationUrlSb.toString());
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new UserException("Mail Failure");
        }
    }

    public void resetPassword(PasswordResetRequest passwordResetRequest) {

    }

}
