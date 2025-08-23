package com.springboot.repository;

import com.springboot.dto.response.JournalLineResponse;
import com.springboot.dto.response.JournalResponse;
import com.springboot.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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

    @Query(value = """
            select
            	j.id as journal_id,
            	j.reference ,
            	j.status ,
            	jl.id as journal_line_id,
            	jl.account_id ,
            	jl.amount ,
            	jl.entry_type
            from
            	journals j
            inner join journal_lines jl on
            	j.id = jl.journal_id
            where
            	j.id = :journalId
            """, nativeQuery = true)
    List<Map<String, Object>> getByJournalId(@Param("journalId") UUID journalId);

    @Query(value = """
            select
            	j.id as journal_id,
            	j.reference ,
            	j.status ,
            	jl.id as journal_line_id,
            	jl.account_id ,
            	jl.amount ,
            	jl.entry_type
            from
            	journals j
            inner join journal_lines jl on
            	j.id = jl.journal_id
            where
            	jl.account_id = :accountId
            """, nativeQuery = true)
    List<Map<String, Object>> getByAccountId(@Param("accountId") UUID accountId);
}
