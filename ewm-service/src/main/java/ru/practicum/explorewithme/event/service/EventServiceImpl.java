package ru.practicum.explorewithme.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.category.service.CategoryService;
import ru.practicum.explorewithme.event.dto.*;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.event.model.AdminEventStateAction;
import ru.practicum.explorewithme.event.model.UserEventStateAction;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exception.EventNotFoundException;
import ru.practicum.explorewithme.exception.InvalidRequestException;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.service.UserService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.explorewithme.constant.Constant.FORMATTER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @Override
    public List<EventShortDto> findEventsOfUser(Long userId, Integer from, Integer size) {
        List<EventShortDto> userEvents = new ArrayList<>();
        User user = userService.findUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsOfUser(userId, pageable).getContent();
        for (Event event : events) {
            EventShortDto dto = EventMapper.toEventShortDto(event);
            dto.setViews(0);
            userEvents.add(dto);
            //ДОБАВИТЬ СТАТИСТИКУ!!!
        }
        log.info("Выполнен поиск событий для пользователя с id {}", userId);
        return userEvents;
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User user = userService.findUserById(userId);
        Category category = categoryService.findCategory(newEventDto.getCategory());
        Event event = EventMapper.toNewEvent(newEventDto, user, category);
        event = eventRepository.save(event);
        log.info("Событие с id {} добавлено", event.getId());
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto findUserEventById(Long userId, Long eventId) {
        Event event = findEventByIdAndInitiatorId(userId, eventId);
        log.info("Выполнен поиск события с id {} и id пользователя {}", eventId, userId);
        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest eventUpdate) {
        Event updated;
        User user = userService.findUserById(userId);
        Event oldEvent = findEventByIdAndInitiatorId(userId, eventId);
        LocalDateTime updateEventTime = LocalDateTime.parse(eventUpdate.getEventDate(), FORMATTER);

        validateEventTimeByUser(updateEventTime);
        updateEventByUserStateAction(oldEvent, eventUpdate);
        updated = eventRepository.save(oldEvent);
        log.info("Событие с id {} пользователя с id {} обновлено", eventId, userId);
        return EventMapper.toEventFullDto(updated);
    }

    @Override
    @Transactional
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest eventUpdate) {
        Event updated;
        Event oldEvent = findEventById(eventId);
        LocalDateTime updateEventTime = LocalDateTime.parse(eventUpdate.getEventDate(), FORMATTER);

        validateEventTimeByAdmin(updateEventTime);
        validateEventState(oldEvent.getState());
        updateEventByAdminStateAction(oldEvent, eventUpdate);
        updated = eventRepository.save(oldEvent);
        log.info("Событие с id {} обновлено", eventId);
        return EventMapper.toEventFullDto(updated);
    }

    @Override
    public List<EventFullDto> findEventsByAdmin(EventAdminParam eventAdminParam) {
        List<Event> events;
        Pageable pageable = PageRequest.of(eventAdminParam.getFrom() / eventAdminParam.getSize(), eventAdminParam.getSize());
        Specification<Event> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (eventAdminParam.getUsers() != null) {
                CriteriaBuilder.In<Long> usersClause = criteriaBuilder.in(root.get("initiator"));
                for (Long user : eventAdminParam.getUsers()) {
                    usersClause.value(user);
                }
                predicates.add(usersClause);
            }
            if (eventAdminParam.getStates() != null) {
                List<EventState> states = getEventStates(eventAdminParam.getStates());
                CriteriaBuilder.In<EventState> statesClause = criteriaBuilder.in(root.get("state"));
                for (EventState state : states) {
                    statesClause.value(state);
                }
                predicates.add(statesClause);
            }
            if (eventAdminParam.getCategories() != null) {
                CriteriaBuilder.In<Long> categoriesClause = criteriaBuilder.in(root.get("category"));
                for (Long category : eventAdminParam.getCategories()) {
                    categoriesClause.value(category);
                }
                predicates.add(categoriesClause);
            }
            if (eventAdminParam.getRangeStart() !=  null) {
                predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), eventAdminParam.getRangeStart()));
            }
            if (eventAdminParam.getRangeEnd() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), eventAdminParam.getRangeEnd()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
                );
        events = eventRepository.findAll(specification, pageable).getContent();
        return EventMapper.toFullDtos(events);
    }

    private void validateEventTimeByAdmin(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidRequestException("Дата начала изменяемого события должна быть не ранее чем за час");
        }
    }

    private void validateEventTimeByUser(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestException("Дата и время на которые намечено событие не может быть " +
                    "раньше, чем через два часа");
        }
    }

    private void validateEventState(EventState state) {
        if (!state.equals(EventState.PENDING)) {
            throw new InvalidRequestException("Событие находится не в состоянии ожидания публикации");
        }
    }

    private void updateEventByAdminStateAction(Event oldEvent, UpdateEventAdminRequest eventUpdate) {
        AdminEventStateAction stateAction;
        try {
            stateAction = AdminEventStateAction.valueOf(eventUpdate.getStateAction());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Неизвестный параметр " + eventUpdate.getStateAction());
        }
        switch (stateAction) {
            case REJECT_EVENT:
                if (oldEvent.getState().equals(EventState.PUBLISHED)) {
                    throw new InvalidRequestException("Невозможно отклонить уже опубликованные события");
                }
                oldEvent.setState(EventState.CANCELED);
                break;
            case PUBLISH_EVENT:
                if (!oldEvent.getState().equals(EventState.PENDING)) {
                    throw new InvalidRequestException("Опубликовать можно только события в состоянии ожидания публикации");
                }
                oldEvent.setState(EventState.PUBLISHED);
                oldEvent.setPublishedOn(LocalDateTime.now());
                break;
            default:
                throw new InvalidRequestException("Неизвестный параметр состояния события");
        }
    }

    private void updateEventByUserStateAction(Event oldEvent, UpdateEventUserRequest eventUpdate) {
        UserEventStateAction stateAction;
        try {
            stateAction = UserEventStateAction.valueOf(eventUpdate.getStateAction());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Неизвестный параметр " + eventUpdate.getStateAction());
        }
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidRequestException("Изменить можно только отмененные события или события в состоянии " +
                    "ожидания модерации ");
        }
        switch (stateAction) {
            case SEND_TO_REVIEW:
                oldEvent.setState(EventState.PENDING);
                break;
            case CANCEL_REVIEW:
                oldEvent.setState(EventState.CANCELED);
                break;
            default:
                throw new InvalidRequestException("Неизвестный параметр состояния события");
        }
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Событие с id "
                + eventId + " не найдено"));
    }

    private Event findEventByIdAndInitiatorId(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Событие с id " + eventId
                        + " и id пользователя " + userId + " не найдено"));
    }

    private List<EventState> getEventStates(List<String> states) {
        EventState eventState;
        List<EventState> eventStates = new ArrayList<>();
        try {
            for (String state : states) {
                eventState = EventState.valueOf(state);
                eventStates.add(eventState);
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Неизвестный параметр состояния события");
        }
        return eventStates;
    }
}
