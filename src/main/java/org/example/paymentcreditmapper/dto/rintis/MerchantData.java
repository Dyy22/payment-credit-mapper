package org.example.paymentcreditmapper.dto.rintis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Data;

@Data @JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantData {
    private String categoryCode;
    private String merchantId;
    private String mPan;

    @JsonProperty("mPan")
    public String getMPan() {
        return mPan;
    }

    @JsonProperty("mPan")
    public void setMPan(String mPan) {
        this.mPan = mPan;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
