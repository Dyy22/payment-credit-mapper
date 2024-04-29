package org.example.paymentcreditmapper.dto.rintis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralRintisRequest {
    private ConsumerData consumerData;
    private MerchantData merchantData;
    private MerchantTransactionStatusResponse merchantTransactionStatusResponse;
    private TransactionData transactionData;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
