package com.example.hotdeal.domain.user.auth.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class UserWithdrawnEvent {

	private final Long userId;

}