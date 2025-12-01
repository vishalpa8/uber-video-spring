package com.personal.uber_video.repository;

import com.personal.uber_video.entity.Captain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CaptainRepository extends JpaRepository<Captain, UUID> {

    boolean existsByEmail(String email);
}