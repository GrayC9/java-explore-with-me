package ru.practicum.explorewithme.request.dto;

import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.RequestStatus;
import ru.practicum.explorewithme.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.constant.Constant.FORMATTER;

public class RequestMapper {

    public static Request toRequest(User user, Event event) {
        return Request.builder()
                .requester(user)
                .event(event)
                .createdAt(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .build();
    }

    public static ParticipationRequestDto toRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreatedAt().format(FORMATTER))
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus().toString())
                .build();
    }

    public static List<ParticipationRequestDto> toDtos(List<Request> requests) {
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }
}
