package com.example.hotdeal.domain.product.notification.domain;

import com.example.hotdeal.domain.event.domain.dto.WSEventProduct;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
public class ListenProductEvent {
    private final Long product_id;
    private final Long event_id;
    private final String notification_message;

    public ListenProductEvent(WSEventProduct event) {
        this.product_id = event.product_id();
        this.event_id = event.event_id();

        DecimalFormat priceFormatter = new DecimalFormat("###,###");
        DecimalFormat discountFormatter = new DecimalFormat("0.##");

        BigDecimal discountPercent = event.discount().multiply(new BigDecimal("100"));

        this.notification_message =
                String.format(
                    "🔥 핫딜 등장! '%s' 지금 바로 %s원! (%s%% 할인)",
                    event.productName(),
                    priceFormatter.format(event.discountPrice()),
                    discountFormatter.format(discountPercent)
                );
    }

    public Notification toNotification() {
        return new Notification(this.product_id, this.event_id, this.notification_message);
    }
}
