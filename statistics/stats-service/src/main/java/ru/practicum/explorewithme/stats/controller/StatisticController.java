package ru.practicum.explorewithme.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.StatisticInDto;
import ru.practicum.explorewithme.StatisticViewDto;
import ru.practicum.explorewithme.stats.service.StatisticService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping(value = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postHit(@Valid @RequestBody StatisticInDto inDto) {
        statisticService.postHit(inDto);
    }

    @GetMapping("/stats")
    public List<StatisticViewDto> getStatistics(@NotEmpty @RequestParam String start,
                                                @NotEmpty @RequestParam String end,
                                                @RequestParam(required = false) List<String> uris,
                                                @RequestParam(value = "unique", defaultValue = "false") String unique) {
        Boolean uniqueParam = Boolean.valueOf(unique);
        return statisticService.getStatistic(start, end, uris, uniqueParam);
    }
}
