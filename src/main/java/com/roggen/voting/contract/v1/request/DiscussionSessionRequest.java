package com.roggen.voting.contract.v1.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionSessionRequest {
    @Builder.Default
    private Long timeout = 1L;
}
