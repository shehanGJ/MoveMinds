package com.java.moveminds.dto.requests.admin;

import com.java.moveminds.enums.Roles;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBulkActionRequest {
    
    @NotEmpty(message = "User IDs list cannot be empty")
    private List<Integer> userIds;
    
    @NotNull(message = "Action is required")
    private BulkAction action;
    
    private Roles newRole;
    private Boolean isVerified;
    
    public enum BulkAction {
        UPDATE_ROLE,
        ACTIVATE_ACCOUNTS,
        DEACTIVATE_ACCOUNTS,
        DELETE_ACCOUNTS,
        EXPORT_DATA
    }
}
