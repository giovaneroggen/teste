package com.roggen.voting.messaging.listener;

import com.roggen.voting.contract.v1.response.DiscussionResultResponse;
import com.roggen.voting.messaging.queue.DiscussionResultQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(name="rabbit.listener.active", havingValue = "true")
@Component
public class DiscussionResultListener {

    @StreamListener(DiscussionResultQueue.DISCUSSION_RESULT_INPUT)
    public void receive(@Payload DiscussionResultResponse response){
        log.debug("DiscussionResultListener.receive", response);
    }
}
