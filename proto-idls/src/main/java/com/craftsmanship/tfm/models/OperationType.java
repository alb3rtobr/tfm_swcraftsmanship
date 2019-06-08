package com.craftsmanship.tfm.models;

public enum OperationType {
    CREATED("CREATED"), EDITED("EDITED"), DELETED("DELETED");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    public static OperationType fromValue(String value) {
        if (value != null) {
            for (OperationType operation : values()) {
                if (operation.value.equals(value)) {
                    return operation;
                }
            }
        }

        throw new IllegalArgumentException("Invalid OperationType: " + value);
    }

    public String toValue() {
        return value;
    }
}