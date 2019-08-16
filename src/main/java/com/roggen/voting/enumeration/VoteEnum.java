package com.roggen.voting.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.roggen.voting.config.exception.GenericException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.EnumSet;

@AllArgsConstructor
public enum VoteEnum {

    YES("yes"), NO("no");

    @Getter
    private String label;

    @JsonValue
    public String jsonValue(){
        return this.label;
    }

    @JsonCreator
    public static VoteEnum jsonCreator(String value){
        return EnumSet.allOf(VoteEnum.class)
                      .stream()
                      .filter(it -> it.getLabel().equalsIgnoreCase(value))
                      .findFirst()
                      .orElse(null);
    }
}
