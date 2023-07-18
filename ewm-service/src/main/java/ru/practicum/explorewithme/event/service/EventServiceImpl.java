package ru.practicum.explorewithme.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.category.service.CategoryService;
import ru.practicum.explorewithme.event.dto.*;
import ru.practicum.explorewithme.event.model.*;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exception.DataConflictException;
import ru.practicum.explorewithme.exception.DataValidationException;
import ru.practicum.explorewithme.exception.EventNotFoundException;
import ru.practicum.explorewithme.exception.InvalidRequestException;
import ru.practicum.explorewithme.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.request.dto.RequestMapper;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.RequestStatus;
import ru.practicum.explorewithme.request.repository.RequestRepository;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.service.UserService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.constant.Constant.FORMATTER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventStatService eventStatService;
    private final RequestRepository requestRepository;

    @Override
    public List<EventShortDto> findEventsOfUser(Long userId, Integer from, Integer size) {
        Map<Long, Long> views;
        List<EventShortDto> userEvents;
        User user = userService.findUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsOfUser(userId, pageable).getContent();
        views = eventStatService.getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        userEvents = EventMapper.toShortDtos(events, views);
        log.info("Выполнен поиск событий для пользователя с id {}", userId);
        return userEvents;
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User user = userService.findUserById(userId);
        Category category = categoryService.findCategory(newEventDto.getCategory());
        Event event = EventMapper.toNewEvent(newEventDto, user, category);
        validateEventTimeByUser(event.getEventDate());
        event = eventRepository.save(event);
        EventFullDto dto = EventMapper.toEventFullDto(event);
        dto.setViews(0L);
        log.info("Событие с id {} добавлено", event.getId());
        return dto;
    }

    @Override
    public EventFullDto findUserEventById(Long userId, Long eventId) {
        Event event = findEventByIdAndInitiatorId(userId, eventId);
        Map<Long, Long> views = eventStatService.getEventsViews(List.of(eventId));
        log.info("Выполнен поиск события с id {} и id пользователя {}", eventId, userId);
        return EventMapper.toEventFullDtoWithViews(event, views);
    }

    @Transactional
    @Override
    public EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest eventUpdate) {
        Event updated;
        Map<Long, Long> views;
        Category category;
        User user = userService.findUserById(userId);
        Event oldEvent = findEventByIdAndInitiatorId(userId, eventId);

        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Изменить можно только отмененные события или события в состоянии " +
                    "ожидания модерации");
        }
        if (eventUpdate.getEventDate() != null) {
            LocalDateTime updateEventTime = LocalDateTime.parse(eventUpdate.getEventDate(), FORMATTER);
            validateEventTimeByUser(updateEventTime);
        }
        if (eventUpdate.getStateAction() != null) {
            updateEventByUserStateAction(oldEvent, eventUpdate);
        }
        if (eventUpdate.getAnnotation() != null) {
            oldEvent.setAnnotation(eventUpdate.getAnnotation());
        }
        if (eventUpdate.getCategory() != null) {
            category = categoryService.findCategory(eventUpdate.getCategory());
            oldEvent.setCategory(category);
        }
        if (eventUpdate.getDescription() != null) {
            oldEvent.setDescription(eventUpdate.getDescription());
        }
        if (eventUpdate.getLocation() != null) {
            oldEvent.setLat(eventUpdate.getLocation().getLat());
            oldEvent.setLon(eventUpdate.getLocation().getLon());
        }
        if (eventUpdate.getPaid() != null) {
            oldEvent.setIsPaid(eventUpdate.getPaid());
        }
        if (eventUpdate.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(eventUpdate.getParticipantLimit());
        }
        if (eventUpdate.getRequestModeration() != null) {
            oldEvent.setRequestModeration(eventUpdate.getRequestModeration());
        }
        if (eventUpdate.getTitle() != null) {
            oldEvent.setTitle(eventUpdate.getTitle());
        }
        updated = eventRepository.save(oldEvent);
        views = eventStatService.getEventsViews(List.of(eventId));
        log.info("Событие с id {} пользователя с id {} обновлено", eventId, userId);
        return EventMapper.toEventFullDtoWithViews(updated, views);
    }

    @Override
    @Transactional
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest eventUpdate) {
        Event updated;
        Map<Long, Long> views;
        Category category;
        Event oldEvent = findEventById(eventId);

        if (eventUpdate.getEventDate() != null) {
            LocalDateTime updateTime = LocalDateTime.parse(eventUpdate.getEventDate(), FORMATTER);
            validateEventTimeByAdmin(updateTime);
        }
        if (eventUpdate.getStateAction() != null) {
            validateEventState(oldEvent.getState());
            updateEventByAdminStateAction(oldEvent, eventUpdate);
        }
        if (eventUpdate.getAnnotation() != null) {
            oldEvent.setAnnotation(eventUpdate.getAnnotation());
        }
        if (eventUpdate.getCategory() != null) {
            category = categoryService.findCategory(eventUpdate.getCategory());
            oldEvent.setCategory(category);
        }
        if (eventUpdate.getDescription() != null) {
            oldEvent.setDescription(eventUpdate.getDescription());
        }
        if (eventUpdate.getLocation() != null) {
            oldEvent.setLat(eventUpdate.getLocation().getLat());
            oldEvent.setLon(eventUpdate.getLocation().getLon());
        }
        if (eventUpdate.getPaid() != null) {
            oldEvent.setIsPaid(eventUpdate.getPaid());
        }
        if (eventUpdate.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(eventUpdate.getParticipantLimit());
        }
        if (eventUpdate.getRequestModeration() != null) {
            oldEvent.setRequestModeration(eventUpdate.getRequestModeration());
        }
        if (eventUpdate.getTitle() != null) {
            oldEvent.setTitle(eventUpdate.getTitle());
        }
        updated = eventRepository.save(oldEvent);
        views = eventStatService.getEventsViews(List.of(eventId));
        log.info("Событие с id {} обновлено администратором", eventId);
        return EventMapper.toEventFullDtoWithViews(updated, views);
    }

    @Override
    public List<EventFullDto> findEventsByAdmin(EventAdminParam eventAdminParam) {
        List<Event> events;
        Map<Long, Long> views;
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
            if (eventAdminParam.getRangeStart() != null) {
                predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), eventAdminParam.getRangeStart()));
            }
            if (eventAdminParam.getRangeEnd() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), eventAdminParam.getRangeEnd()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
        );
        events = eventRepository.findAll(specification, pageable).getContent();
        views = eventStatService.getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        log.info("Выполнен поиск событий для администратора");
        return EventMapper.toFullDtos(events, views);
    }

    @Override
    public List<EventShortDto> findEventsByPublic(EventUserParam eventUserParam, HttpServletRequest request) {
        Sort sort;
        List<Event> events;
        Map<Long, Long> views;
        sort = getEventSort(eventUserParam.getSort());
        Pageable pageable = PageRequest.of(eventUserParam.getFrom() / eventUserParam.getSize(),
                eventUserParam.getSize(), sort);
        LocalDateTime checkedRangeStart = validateRangeTime(eventUserParam.getRangeStart(), eventUserParam.getRangeEnd());
        Specification<Event> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));
            if (eventUserParam.getText() != null) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                                "%" + eventUserParam.getText().toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                                "%" + eventUserParam.getText().toLowerCase() + "%")));
            }
            if (eventUserParam.getCategories() != null) {
                CriteriaBuilder.In<Long> categoriesClause = criteriaBuilder.in(root.get("category"));
                for (Long category : eventUserParam.getCategories()) {
                    categoriesClause.value(category);
                }
                predicates.add(categoriesClause);
            }
            if (eventUserParam.getPaid() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isPaid"), eventUserParam.getPaid()));
            }
            predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), checkedRangeStart));
            if (eventUserParam.getRangeEnd() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), eventUserParam.getRangeEnd()));
            }
            if (eventUserParam.getOnlyAvailable() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("confirmedRequests"), root.get("participantLimit")));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
        );
        events = eventRepository.findAll(specification, pageable).getContent();
        views = eventStatService.getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        log.info("Выполнен публичный поиск опубликованных событий");
        return EventMapper.toShortDtos(events, views);
    }

    @Override
    public EventFullDto findPublishedEventById(Long eventId, HttpServletRequest request) {
        Map<Long, Long> views = eventStatService.getEventsViews(List.of(eventId));
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException("Опубликованного события с указанным id не найдено"));
        log.info("Выполнен публичный поиск опубликованного события с id {}", eventId);
        return EventMapper.toEventFullDtoWithViews(event, views);
    }

    @Override
    public List<ParticipationRequestDto> findUserEventRequests(Long userId, Long eventId) {
        User owner = userService.findUserById(userId);
        Event event = findEventById(eventId);
        List<Request> eventRequests = requestRepository.findAllByEventId(eventId);
        log.info("Выполнен поиск заявок на участие в событии с id {} пользователя {}", eventId, userId);
        return RequestMapper.toDtos(eventRequests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeEventRequestsStatus(Long userId, Long eventId,
                                                                    EventRequestStatusUpdateRequest statusUpdate) {
        int requestsCount = statusUpdate.getRequestIds().size();
        User owner = userService.findUserById(userId);
        Event event = findEventById(eventId);
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        RequestStatus status = RequestStatus.valueOf(statusUpdate.getStatus());
        List<Request> requests = requestRepository.findByIdIn(statusUpdate.getRequestIds());

        if (!Objects.equals(userId, event.getInitiator().getId())) {
            throw new InvalidRequestException("У пользователя с id " + userId + " нет события с id " + eventId);
        }
        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new DataConflictException("Изменить статус можно только у ожидающей подтверждения заявки на " +
                        "участие");
            }
        }
        switch (status) {
            case CONFIRMED:
                if (event.getParticipantLimit() == 0 || !event.getRequestModeration()
                        || event.getParticipantLimit() > event.getConfirmedRequests() + requestsCount) {
                    requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                    event.setConfirmedRequests(event.getConfirmedRequests() + requestsCount);
                    confirmed.addAll(requests);
                } else if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
                    throw new DataConflictException("Достигнут лимит заявок на участие в событии");
                } else {
                    for (Request request : requests) {
                        if (event.getParticipantLimit() > event.getConfirmedRequests()) {
                            request.setStatus(RequestStatus.CONFIRMED);
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                            confirmed.add(request);
                        } else {
                            request.setStatus(RequestStatus.REJECTED);
                            rejected.add(request);
                        }
                    }
                }
                break;
            case REJECTED:
                requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                rejected.addAll(requests);
        }
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(RequestMapper.toDtos(confirmed),
                RequestMapper.toDtos(rejected));
        log.info("Обновлены статусы заявок у события с id {}. Статус {}", eventId, status);
        return result;
    }

    @Override
    public List<Event> findAllByIds(List<Long> ids) {
        return eventRepository.findAllByIdIn(ids);
    }

    private Sort getEventSort(String eventSort) {
        EventSort sort;
        if (eventSort == null) {
            return Sort.by("id");
        }
        try {
            sort = EventSort.valueOf(eventSort);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Некорректный тип сортировки событий");
        }
        switch (sort) {
            case EVENT_DATE:
                return Sort.by("eventDate");
            case VIEWS:
                return Sort.by("views");
            default:
                throw new InvalidRequestException("Некорректный тип сортировки событий");
        }
    }

    private LocalDateTime validateRangeTime(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new DataValidationException("Начало временного промежутка должно быть до его конца");
        } else return Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now);
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
            throw new DataConflictException("Событие находится не в состоянии ожидания публикации");
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
