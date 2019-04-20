package com.craftsmanship.tfm.restapi.kafka.model;

import java.util.Objects;

public class Greetings {
    private long timestamp;
    private String message;

    private Greetings() {
    }

    private Greetings(long timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Greetings timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Greetings message(String message) {
        this.message = message;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Greetings)) {
            return false;
        }
        Greetings greetings = (Greetings) o;
        return timestamp == greetings.timestamp && Objects.equals(message, greetings.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, message);
    }

    @Override
    public String toString() {
        return "{" +
            " timestamp='" + getTimestamp() + "'" +
            ", message='" + getMessage() + "'" +
            "}";
    }

    public static class Builder {

        private long timestamp;
        private String message;

        public Builder() {
        }

        public Builder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Greetings build() {
            Greetings greetings = new Greetings();
            greetings.timestamp = this.timestamp;
            greetings.message = this.message;

            return greetings;
        }
    }
}