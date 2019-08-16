package com.roggen.voting.contract.v1.request;

import com.roggen.voting.enumeration.VoteEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    @NotNull
    private String associateId;
    @NotNull
    private VoteEnum vote;

}
