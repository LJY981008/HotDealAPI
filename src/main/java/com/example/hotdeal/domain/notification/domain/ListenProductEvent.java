package com.example.hotdeal.domain.notification.domain;

import java.text.DecimalFormat;

import com.example.hotdeal.domain.event.domain.dto.WSEventProduct;

import lombok.Getter;

@Getter
public class ListenProductEvent {

	private final Long productId;
	private final Long eventIds;
	private final String notificationMessage;

	public ListenProductEvent(WSEventProduct event) {
		this.productId = event.product_id();
		this.eventIds = event.event_id();

		DecimalFormat priceFormatter = new DecimalFormat("###,###");

		this.notificationMessage =
			String.format(
				"🔥 핫딜 등장! '%s' 지금 바로 %s원! (%s%% 할인)",
				event.productName(),
				priceFormatter.format(event.discountPrice()),
				event.discount()
			);
	}

	public Notification toNotification() {
		return new Notification(this.productId, this.eventIds, this.notificationMessage);
	}

}