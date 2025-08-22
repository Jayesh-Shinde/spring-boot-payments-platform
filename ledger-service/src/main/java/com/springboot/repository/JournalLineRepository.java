package com.springboot.repository;

import com.springboot.entity.JournalLines;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JournalLineRepository extends JpaRepository<JournalLines, UUID> {
}
