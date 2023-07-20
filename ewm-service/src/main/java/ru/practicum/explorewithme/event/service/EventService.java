package ru.practicum.explorewithme.event.service;

import ru.practicum.explorewithme.event.dto.*;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> findEventsOfUser(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto findUserEventById(Long userId, Long eventId);

    EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest eventUpdate);

    EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest eventUpdate);

    List<EventFullDto> findEventsByAdmin(EventAdminParam eventAdminParam);

    List<EventShortDto> findEventsByPublic(EventUserParam eventUserParam, HttpServletRequest request);

    EventFullDto findPublishedEventById(Long eventId, HttpServletRequest request);

    List<ParticipationRequestDto> findUserEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeEventRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<Event> findAllByIds(List<Long> ids);

    List<EventFullDto> findEventsBySubscriptionOfUser(Long userId, Long followerId, Integer from, Integer size);

    List<EventShortDto> findEventsByAllSubscriptions(Long followerId, String sort, Integer from, Integer size);
}
