package ru.practicum.explorewithme.event.dto;

import ru.practicum.explorewithme.category.dto.CategoryMapper;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.event.model.Location;
import ru.practicum.explorewithme.user.dto.UserMapper;
import ru.practicum.explorewithme.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.practicum.explorewithme.constant.Constant.FORMATTER;

public class EventMapper {

    public static Event toNewEvent(NewEventDto newEventDto, User user, Category category) {
        return Event.builder()
                .initiator(user)
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(category)
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER))
                .isPaid(newEventDto.getPaid())
                .title(newEventDto.getTitle())
                .lon(newEventDto.getLocation().getLon())
                .lat(newEventDto.getLocation().getLat())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto fullDto = EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(0)
                .createdOn(event.getCreatedOn().format(FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getIsPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .build();
        if (event.getPublishedOn() != null) {
            fullDto.setPublishedOn(event.getPublishedOn().format(FORMATTER));
        }
        return fullDto;
    }

    public static List<EventFullDto> toFullDtos(Collection<Event> events) {
        List<EventFullDto> dtos = new ArrayList<>();
        for (Event event : events) {
            dtos.add(toEventFullDto(event));
        }
        return dtos;
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate().format(FORMATTER))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getIsPaid())
                .title(event.getTitle())
                .build();
    }
}
