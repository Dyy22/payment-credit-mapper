package org.example.paymentcreditmapper.dto.rintis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import lombok.Data;

@Data @JsonInclude(JsonInclude.Include.NON_NULL)
public class SenderId {
    private String acquiringInstitutionId;
    private String forwardingInstitutionId;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
