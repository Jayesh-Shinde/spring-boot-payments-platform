package com.springboot.repository;

import com.springboot.dto.response.AccountBalances;
import com.springboot.entity.JournalLines;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface JournalLineRepository extends JpaRepository<JournalLines, UUID> {
    @Query(value = """
            select
            	jl.account_id as accountId,
            	(coalesce(SUM(case when jl.entry_type = 'CREDIT' then jl.amount else 0 end ) , 0)
            	- coalesce(SUM(case when jl.entry_type = 'DEBIT' then jl.amount else 0 end), 0) ) as balance
            from
            	journal_lines jl
            group by
            	jl.account_id
            """, nativeQuery = true)
    List<Map<String, Object>> getAllAccountResponse();

    @Query(value = """
            select
            	jl.journal_id
            from
            	journal_lines jl
            group by
            	jl.journal_id
            having
            	SUM(case when jl.entry_type = 'DEBIT' then jl.amount else -amount end)!= 0
            """, nativeQuery = true)
    List<Map<String, Object>> getReconciliation();
}
