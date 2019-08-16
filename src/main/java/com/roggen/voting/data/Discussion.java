package com.roggen.voting.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Document("discussions")
@NoArgsConstructor
@AllArgsConstructor
public class Discussion {
    @Id
    private String id;
    private String discussion;
    @Builder.Default
    private Long yesQuantity = 0L;
    @Builder.Default
    private Long noQuantity = 0L;
    @Builder.Default
    private List<String> associatedList = new ArrayList<>();
    private Long timeout;
    @Builder.Default
    private Boolean active = Boolean.FALSE;
    @Builder.Default
    private Boolean closed = Boolean.FALSE;
    @CreatedDate
    private LocalDateTime createdDate;
    private LocalDateTime activatedDate;
}
