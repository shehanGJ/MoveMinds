package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.dto.AdviserDTO;
import com.java.moveminds.dto.requests.UpdatePasswordRequest;
import com.java.moveminds.dto.requests.UpdateUserRequest;
import com.java.moveminds.dto.response.NonAdvisersResponse;
import com.java.moveminds.dto.response.UserInfoResponse;
import com.java.moveminds.services.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // Endpoint for getting user info
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getUserInfo(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(this.userService.getUserInfo(username));
    }

    // Endpoint for getting user info by id
    @GetMapping("/info-id")
    public ResponseEntity<UserInfoResponse> getUserInfoById(@RequestParam("id") Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(this.userService.getUserInfoById(id));
    }

    // Endpoint for getting user id
    @GetMapping("/user-id")
    public ResponseEntity<Integer> getUserId(@RequestParam("username") String username) {
        return ResponseEntity.ok(userService.getUserId(username));
    }

    // Endpoint for updating user info
    @PatchMapping("/info")
    public ResponseEntity<UserInfoResponse> updateUserInfo(Principal principal, @RequestBody UpdateUserRequest updateUserRequest) {
        String username = principal.getName();
        UserInfoResponse updatedUserInfo = this.userService.updateUserInfo(username, updateUserRequest);
        return ResponseEntity.ok(updatedUserInfo);
    }

    // Endpoint for updating user password
    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(Principal principal, @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        String username = principal.getName();
        this.userService.updatePassword(username, updatePasswordRequest);
        return ResponseEntity.ok("Password changed successfully!");
    }

    // Endpoint for getting user avatar
    @GetMapping("/avatar")
    public ResponseEntity<String> getAvatar(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(this.userService.getAvatar(username));
    }

    // Endpoint for testing if user is active
    @GetMapping("/active")
    public ResponseEntity<Boolean> isActive(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(this.userService.isActive(username));
    }

    // Endpoint for getting all advisers
    @GetMapping("/advisers")
    public ResponseEntity<List<AdviserDTO>> getAdvisers() {
        return ResponseEntity.ok(userService.getAllAdvisers());
    }

    // Endpoint for getting all non-advisers (regular users and admins)
    @GetMapping("/non-advisers")
    public ResponseEntity<List<NonAdvisersResponse>> getNonAdvisers(Principal principal) {
        return ResponseEntity.ok(userService.getAllNonAdvisers(principal));
    }
}
