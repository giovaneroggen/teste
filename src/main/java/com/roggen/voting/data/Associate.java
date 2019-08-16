package com.roggen.voting.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("associates")
@NoArgsConstructor
@AllArgsConstructor
public class Associate {
    @Id
    private String id;
    private String name;
    private String cpf;
}
