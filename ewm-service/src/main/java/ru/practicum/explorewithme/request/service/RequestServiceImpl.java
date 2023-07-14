package ru.practicum.explorewithme.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exception.EventNotFoundException;
import ru.practicum.explorewithme.exception.RequestNotFoundException;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.request.dto.RequestMapper;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.RequestStatus;
import ru.practicum.explorewithme.request.repository.RequestRepository;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> findUserRequests(Long userId) {
        User requester = userService.findUserById(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Выполнен поиск запросов на участие пользователя {}", userId);
        return RequestMapper.toDtos(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId, ParticipationRequestDto requestDto) {
        User requester = userService.findUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Событие, указанное для запроса, не найдено"));
        Request request = requestRepository.save(RequestMapper.toRequest(requester, event));
        log.info("Добавлен новый запрос на участие в событии {} пользователя {}", requestDto.getEvent(), userId);
        return RequestMapper.toRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectRequest(Long userId, Long requestId) {
        User requester = userService.findUserById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос для отклонения не найден"));
        request.setStatus(RequestStatus.CANCELLED);
        request = requestRepository.save(request);
        log.info("Запрос на участие с id {} был отменён", requestId);
        return RequestMapper.toRequestDto(request);
    }
}
