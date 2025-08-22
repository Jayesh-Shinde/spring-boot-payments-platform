package com.springboot.repository;

import com.springboot.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface JournalRepository extends JpaRepository<Journal, UUID> {
    Optional<Journal> findByIdempotencyKey(String idempotencyKey);

    @Query(value = """
            select
            	coalesce(SUM(case when jl.entry_type = 'CREDIT' then jl.amount else 0 end), 0) -\s
            coalesce(SUM(case when jl.entry_type = 'DEBIT' then jl.amount else 0 end), 0)
            from
            	journal_lines jl
            where
            	jl.account_id = :accountId
            group by
            	jl.account_id
            """, nativeQuery = true)
    BigDecimal getAccountBalance(@Param("accountId") UUID accountId);
}
