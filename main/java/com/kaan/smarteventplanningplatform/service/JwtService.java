/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.service;

import com.kaan.smarteventplanningplatform.model.Token;
import com.kaan.smarteventplanningplatform.model.User;
import com.kaan.smarteventplanningplatform.repo.TokenRepo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author kaan
 */
@Service
public class JwtService {

    private static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(JwtService.class);
    }

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expirationInMs;

    private TokenRepo tokenRepo;

    private UserService userService;


    public JwtService(TokenRepo tokenRepo, UserService userService) {
        this.tokenRepo = tokenRepo;
        this.userService = userService;
    }

    /*
    Bu metot kullanici icin veritabaninda token yoksa token uretir ve kaydeder
    Eger token varsa tokenda bir problem olup olmadigini kontrol eder 
    Eger problem varsa veritabanindaki token silinip yeni token olusturulur.
    Problem yoksa eski token kullanilmaya devam edilir .
     */
    @Transactional
    public String getTokenByUserId(Long userId) {
        Optional<Token> tokenOptional = tokenRepo.findByUserId(userId);
        String token = null;
        if (tokenOptional.isPresent()) {
            token = tokenOptional.get().getToken();
            try {
                validate(token);
            } catch (JwtException | IllegalArgumentException ex) {
                removeToken(token);
                token = createNewTokenAndSave(userId).getToken();
            }
        } else {
            token = createNewTokenAndSave(userId).getToken();
        }
        return token;
    }



    /*
    Bu metot eger tokenda problem varsa exception firlatir.
     */
    public void validate(String token) throws JwtException, IllegalArgumentException {
        Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    /*
    Kullanici ID bilgisini almak icin kullanilacak metot.
    Not : Egerki jwt expiry olmussa bile claimsdeki id bilgisini verir .
     */
    public Long getUserIdByToken(String token) {
        Long userId = null;
        try {
            userId = Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload().get("id", Long.class);
        } catch (ExpiredJwtException ex) {
            userId = ex.getClaims().get("id", Long.class);
        }
        return userId;
    }

    public Long getUserIdByNotParsedToken(String notParsedToken) {
        Long userId = null;
        String parsedToken = null;
        if (notParsedToken != null) {
            try {
                notParsedToken = URLDecoder.decode(notParsedToken, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return null;
            }
            parsedToken = notParsedToken.substring(7);
            userId = getUserIdByToken(parsedToken);
        }
        return userId;
    }

    public String parseToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("Authorization")) {
                        authorization = cookie.getValue();
                        break;
                    }
                }
            }
        }
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer")) {
            token = authorization.substring(7);
        }
        return token;
    }
    
    public void deleteByUserId (Long userId) {
        tokenRepo.deleteByUserId(userId);
    }

    /*
    Yeni token olusturmaya ve veritabanina kaydetmeye yarar.
     */
    @Transactional
    private Token createNewTokenAndSave(Long userId) {
        User user = userService.getUserById(userId);

        Map<String, Object> claims = new HashMap();
        claims.put("sub", user.getUsername());
        claims.put("id", user.getId());

        Date issuedAt = new Date();
        Date expirationDate = new Date(System.currentTimeMillis() + Long.parseLong(expirationInMs));
        claims.put("iat", issuedAt);
        claims.put("exp", expirationDate);

        String tokenCode = Jwts.builder()
                .claims(claims)
                .signWith(getSecretKey())
                .compact();

        Token token = new Token();
        token.setIsExpired(Boolean.FALSE);
        token.setToken(tokenCode);
        token.setUser(user);
        tokenRepo.save(token);
        return token;
    }

    private void removeToken(String token) {
        tokenRepo.deleteByToken(token);
    }

    private SecretKey getSecretKey() {
        SecretKey key = Keys.hmacShaKeyFor(decodeEncodedSecret());
        return key;
    }

    private byte[] decodeEncodedSecret() {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        return decodedKey;
    }

}
