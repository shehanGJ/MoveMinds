package com.java.moveminds.services;

import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface LogService {
    void log(Principal principal, String action);
}
