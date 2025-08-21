package com.springboot.dto.response;

import com.springboot.dto.enums.JournalStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;


@RequiredArgsConstructor
@Data
public class JournalResponse {

    @org.hibernate.validator.constraints.UUID
    @NotNull
    private UUID id;
    @Size(min = 5, max = 100)
    @NotNull
    private String reference;
    @Size(min = 6, max = 8)
    @NotNull
    private JournalStatus journalStatus;

    public JournalResponse(UUID id, String reference, JournalStatus journalStatus) {
        this.id = id;
        this.reference = reference;
        this.journalStatus = journalStatus;
    }

}
