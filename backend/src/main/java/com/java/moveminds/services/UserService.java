package com.java.moveminds.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.java.moveminds.models.dto.AdviserDTO;
import com.java.moveminds.models.dto.requests.UpdatePasswordRequest;
import com.java.moveminds.models.dto.requests.UpdateUserRequest;
import com.java.moveminds.models.dto.response.NonAdvisersResponse;
import com.java.moveminds.models.dto.response.UserInfoResponse;

import java.security.Principal;
import java.util.List;

@Service
public interface UserService extends UserDetailsService  {
    UserInfoResponse getUserInfo(String username);
    UserInfoResponse updateUserInfo(String username, UpdateUserRequest userInfoResponse);
    void updatePassword(String username, UpdatePasswordRequest updatePasswordRequest);
    String getAvatar(String username);
    Boolean isActive(String username);
    Integer getUserId(String username);
    UserInfoResponse getUserInfoById(Integer id);
    List<AdviserDTO> getAllAdvisers();
    List<NonAdvisersResponse> getAllNonAdvisers(Principal principal);
}
