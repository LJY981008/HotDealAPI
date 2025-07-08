package com.example.hotdeal.domain.product.product.domain;

import com.example.hotdeal.domain.product.product.exception.ProductException;
import lombok.Getter;

@Getter
public enum ProductCategory {
    ELECTRONICS("전자제품"),
    FASHION("패션/의류"),
    BEAUTY("뷰티/화장품"),
    HOME_LIVING("홈/리빙"),
    FOOD("식품"),
    SPORTS("스포츠/레저"),
    BOOKS("도서"),
    DIGITAL("디지털/가전"),
    HEALTH("건강/의료"),
    BABY("육아/출산"),
    PET("반려동물"),
    CAR("자동차/용품"),
    HOBBY("취미/수집"),
    OFFICE("사무/문구"),
    OTHER("기타");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public static ProductCategory validateAndGet(ProductCategory category) {
        if (category == null) {
            throw ProductException.invalidCategory();
        }
        return category;
    }
}
