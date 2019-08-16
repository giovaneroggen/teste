package com.roggen.voting.contract.v1.mapper;

import com.roggen.voting.contract.v1.request.DiscussionRequest;
import com.roggen.voting.contract.v1.request.DiscussionSessionRequest;
import com.roggen.voting.contract.v1.request.VoteRequest;
import com.roggen.voting.contract.v1.response.DiscussionResponse;
import com.roggen.voting.contract.v1.response.DiscussionResultResponse;
import com.roggen.voting.data.Discussion;
import com.roggen.voting.enumeration.VoteEnum;

import java.time.LocalDateTime;

public class DiscussionMapper {

    public static Discussion map(DiscussionRequest request){
        return Discussion.builder()
                         .discussion(request.getDiscussion())
                         .build();
    }

    public static DiscussionResponse map(Discussion discussion){
        return DiscussionResponse.builder()
                .id(discussion.getId())
                .discussion(discussion.getDiscussion())
                .createdDate(discussion.getCreatedDate())
                .build();
    }

    public static Discussion map(Discussion d, DiscussionSessionRequest request) {
        d.setActive(Boolean.TRUE);
        d.setTimeout(request.getTimeout());
        d.setActivatedDate(LocalDateTime.now());
        return d;
    }

    public static Discussion map(Discussion d, VoteRequest request) {
        d.getAssociatedList().add(request.getAssociateId());
        addVote(d, request.getVote());
        return d;
    }

    public static DiscussionResultResponse mapResult(Discussion discussion){
        return DiscussionResultResponse.builder()
                .id(discussion.getId())
                .discussion(discussion.getDiscussion())
                .noQuantity(discussion.getNoQuantity())
                .yesQuantity(discussion.getYesQuantity())
                .total(discussion.getNoQuantity() + discussion.getYesQuantity())
                .build();
    }

    private static void addVote(Discussion d, VoteEnum vote) {
        switch (vote){
            case YES:
                d.setYesQuantity(d.getYesQuantity() + 1L);
                break;
            case NO:
                d.setNoQuantity(d.getNoQuantity() + 1L);
                break;
        }
    }
}
