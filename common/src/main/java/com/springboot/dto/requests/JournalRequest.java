package com.springboot.dto.requests;


import com.springboot.dto.enums.JournalStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class JournalRequest {
    @Size(min = 5, max = 100)
    private String reference;
    @NotNull
    private JournalStatus journalStatus;
    private List<JournalLineRequest> journalLineRequests = new ArrayList<>();
}
