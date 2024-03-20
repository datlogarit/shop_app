package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.User;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseForOrder {
    @JsonProperty("user_id")
    private Long userId;

    public static UserResponseForOrder fromUser(User user){
        return UserResponseForOrder.builder()
                .userId(user.getId())
                .build();
    }
}
