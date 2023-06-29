package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.StatisticViewDto;
import ru.practicum.explorewithme.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    @Query("SELECT new ru.practicum.explorewithme.StatisticViewDto(s.app, s.uri, COUNT (s.ip))" +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<StatisticViewDto> findAllStatisticsByTime(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explorewithme.StatisticViewDto(s.app, s.uri, COUNT (DISTINCT s.ip))" +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<StatisticViewDto> findAllStatisticsByTimeAndUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explorewithme.StatisticViewDto(s.app, s.uri, COUNT (DISTINCT s.ip))" +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<StatisticViewDto> findAllStatisticsByTimeAndListOfUrisAndUniqueIp(
            LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.explorewithme.StatisticViewDto(s.app, s.uri, COUNT (s.ip))" +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<StatisticViewDto> findAllStatisticsByTimeAndListOfUris(LocalDateTime start, LocalDateTime end,
                                                                List<String> uris);
}
