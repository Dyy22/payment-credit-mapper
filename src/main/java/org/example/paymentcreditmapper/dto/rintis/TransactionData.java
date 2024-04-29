package org.example.paymentcreditmapper.dto.rintis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionData {
    private String additionalData;
    private String additionalDataNational;
    private String amountFee;
    private String approvalCode;
    private String captureDate;
    private String currency;
    private String localTransactionDate;
    private String localTransactionTime;
    private String pointOfServiceEntryMode;
    private String processingCode;
    private String rrn;
    private String settlementDate;
    private String stan;
    private Integer totalAmount;
    private String transmissionDateTime;
    private CardAcceptorData cardAcceptorData;
    private SenderId senderId;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
