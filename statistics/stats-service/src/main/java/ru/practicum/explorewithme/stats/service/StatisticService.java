package ru.practicum.explorewithme.stats.service;

import ru.practicum.explorewithme.StatisticInDto;
import ru.practicum.explorewithme.StatisticViewDto;

import java.util.List;

public interface StatisticService {

    void postHit(StatisticInDto inDto);

    List<StatisticViewDto> getStatistic(String start, String end, List<String> uris, Boolean unique);
}
