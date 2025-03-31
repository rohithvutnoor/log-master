package io.github.logmaster.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogDetails {
    String message;
    Object[] parameters;
}