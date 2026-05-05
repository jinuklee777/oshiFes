package com.oshifes.domain.event.dao;

import com.oshifes.domain.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<Event> findByIdAndDeletedAtIsNull(Long id);
}
