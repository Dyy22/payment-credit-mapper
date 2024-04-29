package org.example.paymentcreditmapper.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import lombok.Data;
import org.example.paymentcreditmapper.dto.rintis.ConsumerData;
import org.example.paymentcreditmapper.dto.rintis.MerchantData;
import org.example.paymentcreditmapper.dto.rintis.MerchantTransactionStatusResponse;
import org.example.paymentcreditmapper.dto.rintis.TransactionData;

@Data
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
