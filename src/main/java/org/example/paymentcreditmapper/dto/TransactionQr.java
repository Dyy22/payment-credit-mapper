package org.example.paymentcreditmapper.dto;

import com.google.gson.Gson;
import lombok.*;


@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionQr {
    private Long transactionQrId;
    private Long transactionHeaderId;
    private String invoiceNumber;
    private String cPan;
    private String issuerId;
    private String categoryCode;
    private String merchantId;
    private String mPan;
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
    private String totalAmount;
    private String transmissionDateTime;
    private String cardAcceptorCity;
    private String cardAcceptorCountryCode;
    private String cardAcceptorName;
    private String cardAcceptorTerminalID;
    private String acquiringInstitutionId;
    private String forwardingInstitutionId;
    private String responseStatus;
    private String acquirer;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
