package com.audrey.refeat.domain.user.entity;

public enum Authority {
    USER,
    ADMIN;

    public Authority getEnum(String value) {
        return valueOf(value.toUpperCase());
    }
}
