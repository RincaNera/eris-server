package com.rinca.erisserver.dto;

import java.util.Optional;

public class TopicEditRequest {
    private Optional<String> name = Optional.empty();

    public Optional<String> getName() {
        return name;
    }

    public void setName(Optional<String> name) {
        this.name = name;
    }
}
