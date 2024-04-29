package org.example.paymentcreditmapper.dto;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class QrisTranslateRequest {
    private String qr;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
