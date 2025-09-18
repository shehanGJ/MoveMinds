package com.java.moveminds.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.entities.LogEntity;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.repositories.LogEntityRepository;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.services.LogService;

import java.security.Principal;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final UserEntityRepository userRepository;
    private final LogEntityRepository logRepository;

    @Override
    public void log(Principal principal, String action) {
        String displayName = "System user";
        if (principal != null) {
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(UserNotFoundException::new);
            displayName = getDisplayName(user);
        }

        LogEntity newLog = new LogEntity();
        newLog.setUser(displayName);
        newLog.setAction(action);
        newLog.setTimestamp(new Timestamp(System.currentTimeMillis()));

        logRepository.saveAndFlush(newLog);


    }

    private static String getDisplayName(UserEntity user) {
        if (user == null) {
            return "";
        }
        String displayName;
        if (user.getFirstName() != null && user.getLastName() != null) {
            displayName = user.getFirstName() + " " + user.getLastName();
        } else if (user.getFirstName() != null) {
            displayName = user.getFirstName();
        } else if (user.getLastName() != null) {
            displayName = user.getLastName();
        } else {
            displayName = user.getUsername();
        }
        return displayName;
    }
}
