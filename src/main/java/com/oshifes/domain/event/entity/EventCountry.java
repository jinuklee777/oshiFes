package com.oshifes.domain.event.entity;

public enum EventCountry {
    KR, JP;

    public static final String REGEXP = "KR|JP";
    public static final String VALIDATION_MESSAGE = "country는 KR 또는 JP여야 합니다.";
}
