package com.roggen.voting.contract.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionSessionResponse {
    private String id;
    private DiscussionResponse discussion;
    private Long timeout = 1L;
    private Boolean closed = Boolean.FALSE;
    private LocalDateTime createdDate;
}
