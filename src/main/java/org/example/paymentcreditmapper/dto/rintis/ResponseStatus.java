package org.example.paymentcreditmapper.dto.rintis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import lombok.Data;

@Data @JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseStatus {
    private String reason;
    private String responseCode;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
