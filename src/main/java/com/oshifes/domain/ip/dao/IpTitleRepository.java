package com.oshifes.domain.ip.dao;

import com.oshifes.domain.ip.entity.IpTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IpTitleRepository extends JpaRepository<IpTitle, Long> {

    Optional<IpTitle> findBySourceTypeAndExternalId(String sourceType, String externalId);
}
