package com.java.moveminds.dto.requests;

import com.java.moveminds.enums.Roles;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    private Roles role;
}
