package com.mb.prestartcheck.fins;

public enum ResultEnum {
    RESULT_SUCCESS ("Success"),
    RESULT_FAIL ("Fail");

    private String value;

    ResultEnum(String value) {
        this.value = value;
    }
}
