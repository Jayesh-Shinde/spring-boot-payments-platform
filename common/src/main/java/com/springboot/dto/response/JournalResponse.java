package com.springboot.dto.response;

import com.springboot.dto.enums.JournalStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
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
    @NotNull
    private List<JournalLineResponse> journalLineResponses;


}
