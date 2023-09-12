package com.unitech.api.repository;

import com.unitech.api.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByPin(String pin);

    public Optional<UserEntity> findByPin(String pin);

    public Page<UserEntity> findByStatusId(int statusId, Pageable pageable);



}
