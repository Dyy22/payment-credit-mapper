package org.example.paymentcreditmapper.dto.rintis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"cPan", "issuerId"})
public class ConsumerData {
    private String cPan;

    @Getter @Setter
    private String issuerId;

    @JsonProperty("cPan")
    public String getCPan() {
        return cPan;
    }

    @JsonProperty("cPan")
    public void setCPan(String cPan) {
        this.cPan = cPan;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
