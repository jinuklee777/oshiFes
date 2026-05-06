package com.oshifes.domain.event.dao;

import com.oshifes.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Optional<Event> findByIdAndDeletedAtIsNull(Long id);
}
