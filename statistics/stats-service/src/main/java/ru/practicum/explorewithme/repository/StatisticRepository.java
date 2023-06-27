package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.Statistic;

@Repository
public interface StatisticRepository extends JpaRepository<Long, Statistic> {
}
