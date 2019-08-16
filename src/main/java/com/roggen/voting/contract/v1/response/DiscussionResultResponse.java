package com.roggen.voting.contract.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionResultResponse {
    private String id;
    private String discussion;
    private Long total;
    private Long yesQuantity;
    private Long noQuantity;
}
