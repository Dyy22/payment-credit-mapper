package org.example.paymentcreditmapper.dto.rintis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @JsonInclude(JsonInclude.Include.NON_NULL)
public class CardAcceptorData {
    private String cardAcceptorCity;
    private String cardAcceptorCountryCode;
    private String cardAcceptorName;
    private String cardAcceptorTerminalID;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
