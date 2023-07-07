package ru.practicum.explorewithme.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ApiError {

    private final HttpStatus status;
    private final String reason;
    private final String message;
    private final List<String> errors;
}
