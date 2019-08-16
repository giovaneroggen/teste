package com.roggen.voting.contract.v1.mapper;

import com.roggen.voting.contract.v1.request.AssociateRequest;
import com.roggen.voting.contract.v1.response.AssociateResponse;
import com.roggen.voting.data.Associate;

public class AssociateMapper {

    public static Associate map(AssociateRequest request){
        return Associate.builder()
                         .name(request.getName())
                         .cpf(request.getCpf())
                         .build();
    }

    public static AssociateResponse map(Associate associate){
        return AssociateResponse.builder()
                .id(associate.getId())
                .name(associate.getName())
                .cpf(associate.getCpf())
                .build();
    }
}
