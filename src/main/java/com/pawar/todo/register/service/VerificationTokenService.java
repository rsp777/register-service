package com.pawar.todo.register.service;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pawar.todo.register.entity.User;
import com.pawar.todo.register.entity.VerificationToken;
import com.pawar.todo.register.repository.VerificationTokenRepository;

@Service
public class VerificationTokenService {
	
    private static final Logger logger = LoggerFactory.getLogger(VerificationTokenService.class);

	
	@Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public VerificationToken createVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryDate(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000))); // 24 hours
        
        logger.info("Verification Token : "+verificationToken.toString());
        
        return verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    public void confirmToken(VerificationToken token) {
        token.setVerified(true);
        verificationTokenRepository.save(token);
    }
}
