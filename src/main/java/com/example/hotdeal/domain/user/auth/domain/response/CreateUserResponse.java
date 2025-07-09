package com.example.hotdeal.domain.user.auth.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class CreateUserResponse {

    private final Long authId;
    private final String email;
    private final String name;

}
