package com.example.hotdeal.domain.user.profile.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class UserRestoreFailedEvent {
	private final Long authId;

}
