package com.example.hotdeal.domain.user.profile.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class UserSaveFailedEvent {

	private final Long authId;

}