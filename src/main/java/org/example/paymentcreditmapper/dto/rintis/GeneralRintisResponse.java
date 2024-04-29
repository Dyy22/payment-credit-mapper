package org.example.paymentcreditmapper.dto.rintis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralRintisResponse {
    private ConsumerData consumerData;
    private MerchantData merchantData;
    private MerchantTransactionStatusResponse merchantTransactionStatusResponse;
    private TransactionData transactionData;
    private ResponseStatus responseStatus;
    private ErrorResponse res;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
