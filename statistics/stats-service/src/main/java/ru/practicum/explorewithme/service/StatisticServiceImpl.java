package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.StatisticInDto;
import ru.practicum.explorewithme.StatisticViewDto;
import ru.practicum.explorewithme.exception.StatisticValidationException;
import ru.practicum.explorewithme.model.StatisticMapper;
import ru.practicum.explorewithme.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static ru.practicum.explorewithme.constant.Constant.TIME_FORMAT;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Transactional
    @Override
    public void postHit(StatisticInDto inDto) {
        statisticRepository.save(StatisticMapper.toStatistic(inDto));
        log.info("Сохранение новой записи статистики");
    }

    @Override
    public List<StatisticViewDto> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = parseTimeParam(start);
        LocalDateTime endTime = parseTimeParam(end);
        List<StatisticViewDto> dtos;

        if (uris != null) {
            if (unique) {
                dtos = statisticRepository.findAllStatisticsByTimeAndListOfUrisAndUniqueIp(startTime, endTime, uris);
            } else {
                dtos = statisticRepository.findAllStatisticsByTimeAndListOfUris(startTime, endTime, uris);
            }
        } else if (unique) {
            dtos = statisticRepository.findAllStatisticsByTimeAndUniqueIp(startTime, endTime);
        } else {
            dtos = statisticRepository.findAllStatisticsByTime(startTime, endTime);
        }
        log.info("Выполнение сбора статистики");
        return dtos;
    }

    private LocalDateTime parseTimeParam(String time) {
        try {
            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(TIME_FORMAT));
        } catch (DateTimeParseException e) {
            throw new StatisticValidationException("Передан некорректный формат времени");
        }
    }
}
