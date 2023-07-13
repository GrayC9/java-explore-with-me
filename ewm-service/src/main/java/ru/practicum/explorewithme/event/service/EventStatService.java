package ru.practicum.explorewithme.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.StatisticClient;
import ru.practicum.explorewithme.StatisticViewDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.explorewithme.constant.Constant.EVENT_URI;
import static ru.practicum.explorewithme.constant.Constant.FORMATTER;

@Component
@RequiredArgsConstructor
public class EventStatService {

    private final StatisticClient statisticClient;
    private final ObjectMapper objectMapper;
    private final Gson gson;

    public Map<Long, Long> getEventsViews(List<Long> events) {
        int counter = 0;
        List<StatisticViewDto> stats;
        Map<Long, Long> eventsViews = new HashMap<>();
        List<String> uris = new ArrayList<>();

        if (events == null || events.isEmpty()) {
            return eventsViews;
        }
        for (Long id : events) {
            uris.add(EVENT_URI + id);
        }
        ResponseEntity<Object> response = statisticClient.getStatistics(LocalDateTime.now().minusDays(100).format(FORMATTER),
                LocalDateTime.now().format(FORMATTER), uris, false);
        Object body = response.getBody();
        if (body != null) {
            String json = gson.toJson(body);
            TypeReference<List<StatisticViewDto>> typeRef = new TypeReference<>() {
            };
            try {
                stats = objectMapper.readValue(json, typeRef);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка при загрузке данных из сервиса статистики");
            }
            if (stats.isEmpty()) {
                for (Long event : events) {
                    eventsViews.put(event, 0L);
                }
            } else {
                for (Long event : events) {
                    eventsViews.put(event, stats.get(counter).getHits());
                    counter++;
                }
            }
        }
        return eventsViews;
    }
}
