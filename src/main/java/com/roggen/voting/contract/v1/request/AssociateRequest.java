package com.roggen.voting.contract.v1.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociateRequest {

    @NotNull
    @NotEmpty
    private String name;
    @CPF
    @NotNull
    private String cpf;
}
