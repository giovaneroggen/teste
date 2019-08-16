package com.roggen.voting.messaging.queue;


import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface DiscussionResultQueue {
    String DISCUSSION_RESULT = "discussion-result";
    String DISCUSSION_RESULT_INPUT  = DISCUSSION_RESULT+"-input";
    String DISCUSSION_RESULT_OUTPUT = DISCUSSION_RESULT+"-output";

    @Output(DISCUSSION_RESULT_OUTPUT)
    MessageChannel output();

    @Input(DISCUSSION_RESULT_INPUT)
    SubscribableChannel input();
}
