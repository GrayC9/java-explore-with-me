package ru.practicum.explorewithme.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findByIdInAndStatus(List<Long> requestIds, RequestStatus status);
}
