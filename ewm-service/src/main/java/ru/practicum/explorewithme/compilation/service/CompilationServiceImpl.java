package ru.practicum.explorewithme.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.CompilationMapper;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.compilation.repository.CompilationRepository;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.service.EventService;
import ru.practicum.explorewithme.event.service.EventStatService;
import ru.practicum.explorewithme.exception.CompilationNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final EventStatService statService;

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        Set<Event> eventSet = new HashSet<>();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllByIsPinned(pinned, pageable);
        for (Compilation compilation : compilations) {
            eventSet.addAll(compilation.getEvents());
        }
        List<Long> eventIds = eventSet.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> views = statService.getEventsViews(eventIds);
        log.info("Выполнен запрос на поиск подборок событий");
        return CompilationMapper.toDtos(compilations, views);
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Подборка с id " + compId + " не найдена"));
        List<Long> events = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> views = statService.getEventsViews(events);
        log.info("Выполнен поиск подборки событий по id {}", compId);
        return CompilationMapper.toCompilationDto(compilation, views);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        List<Event> events;
        Map<Long, Long> views = new HashMap<>();
        if (compilationDto.getEvents() != null) {
            events = eventService.findAllByIds(compilationDto.getEvents());
            views = statService.getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        } else {
            events = new ArrayList<>();
        }
        Compilation compilation = CompilationMapper.toNewCompilation(compilationDto, events);
        compilation = compilationRepository.save(compilation);
        log.info("Добавлена новая подборка событий");
        return CompilationMapper.toCompilationDto(compilation, views);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
            log.info("Подборка с id " + compId + "не найдена");
        }
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest update) {
        Compilation oldCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Подборка с id " + compId + " не найдена"));
        Map<Long, Long> views = new HashMap<>();
        if (update.getEvents() != null && !update.getEvents().isEmpty()) {
            List<Event> events = eventService.findAllByIds(update.getEvents());
            oldCompilation.setEvents(events);
            views = statService.getEventsViews(update.getEvents());
        }
        if (update.getPinned() != null) {
            oldCompilation.setIsPinned(update.getPinned());
        }
        if (update.getTitle() != null) {
            oldCompilation.setTitle(update.getTitle());
        }
        log.info("Подборка с id {} была обновлена", compId);
        return CompilationMapper.toCompilationDto(compilationRepository.save(oldCompilation), views);
    }
}
