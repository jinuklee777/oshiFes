package com.oshifes.domain.event.dao;

import com.oshifes.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByDeletedAtIsNull();

    Optional<Event> findByIdAndDeletedAtIsNull(Long id);
}
