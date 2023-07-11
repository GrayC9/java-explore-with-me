package ru.practicum.explorewithme.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.dto.*;
import ru.practicum.explorewithme.event.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explorewithme.constant.Constant.TIME_FORMAT;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    //Private endpoints
    @PostMapping(value = "/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> findEventsOfUser(@PathVariable Long userId,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.findEventsOfUser(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto findUserEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.findUserEventById(userId, eventId);
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}")
    public EventFullDto userUpdateEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.userUpdateEvent(userId, eventId, updateEventUserRequest);
    }

    //Admin endpoints
    @GetMapping("/admin/events")
    public List<EventFullDto> findEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeEnd,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        EventAdminParam eventAdminParam = new EventAdminParam(users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.findEventsByAdmin(eventAdminParam);
    }

    @PatchMapping(value = "/admin/events/{eventId}")
    public EventFullDto adminUpdateEvent(@PathVariable Long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        return eventService.adminUpdateEvent(eventId, updateRequest);
    }
}
