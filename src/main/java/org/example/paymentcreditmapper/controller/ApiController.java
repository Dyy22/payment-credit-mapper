package org.example.paymentcreditmapper.controller;

import com.emv.qrcode.decoder.mpm.DecoderMpm;
import com.emv.qrcode.model.mpm.MerchantAccountInformationTemplate;
import com.emv.qrcode.model.mpm.MerchantPresentedMode;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.paymentcreditmapper.dto.GeneralRintisRequest;
import org.example.paymentcreditmapper.dto.QrisTranslateRequest;
import org.example.paymentcreditmapper.dto.TransactionQr;
import org.example.paymentcreditmapper.dto.rintis.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @PostMapping(path = "/qris/translate")
    public GeneralRintisRequest qrisTranslate(
            @RequestBody QrisTranslateRequest request
    ){
        log.info("[START] Rintis Qr Translate Request : {}", request);

        MerchantPresentedMode mpm = DecoderMpm.decode(request.getQr(), MerchantPresentedMode.class);
        TransactionQr transactionQr = createTransactionQr(mpm);
        GeneralRintisRequest response = qrisRequestBuilder(transactionQr);

        log.info("[END] Rintis Qr Translate Response : {}", response);
        return response;
    }

    private TransactionQr createTransactionQr(MerchantPresentedMode mpm) {
        String currency = mpm.getTransactionCurrency().getValue();
        String merchantId = "";
        String mPan = "";
        String processingCode = "370000";
        String approvalCode =  RandomStringUtils.randomAlphanumeric(6);
        StringBuilder additionalData = new StringBuilder();
        String acquiringInstitutionId = "93600000";
        String cPan = "9360084478716133166";
        cPan = cPan.concat(validCardNumber(cPan));

        StringBuilder additionalDataNational = new StringBuilder();
        additionalDataNational.append(mpm.getPostalCode().getTag());
        additionalDataNational.append(String.format("%02d", mpm.getPostalCode().getLength()));
        additionalDataNational.append(mpm.getPostalCode().getValue());

        if (mpm.getAdditionalDataField() != null) {
            additionalDataNational.append(mpm.getAdditionalDataField().getTag());
            additionalDataNational.append(String.format("%02d", mpm.getAdditionalDataField().getLength()));
            additionalDataNational.append(mpm.getAdditionalDataField().getValue());
        }

        if (mpm.getMerchantAccountInformation().get("51") != null) {
            HashMap<String, String> tlvData = parseTlv(mpm.getMerchantAccountInformation().get("51").getValue().toString());
            log.info("QR With Tag 51, Data : {}", tlvData);

            merchantId = tlvData.get("02");
            mPan = "9" + currency + "0000" + merchantId.substring(4);
            additionalData.append("PI04IQ01");
        }

        MerchantAccountInformationTemplate accountInformation = null;
        for (int i = 26; i <= 45; i++) {
            MerchantAccountInformationTemplate accountInformationTemplate = mpm.getMerchantAccountInformation().get(String.valueOf(i));
            if (accountInformationTemplate != null) {
                HashMap<String, String> tlvData = parseTlv(accountInformationTemplate.getValue().toString());
                log.info("QR With Tag {}, Data : {}", i, tlvData);

                mPan = tlvData.get("01").concat(validCardNumber(tlvData.get("01")));

                if (accountInformation == null || mPan.substring(0, 8).equals("93600844")) {
                    accountInformation = accountInformationTemplate;
                }
            }
        }

        if (accountInformation != null) {
            additionalData = new StringBuilder();
            HashMap<String, String> tlvData = parseTlv(accountInformation.getValue().toString());

            merchantId = tlvData.get("02");
            mPan = tlvData.get("01").concat(validCardNumber(tlvData.get("01")));
            processingCode = "261000";

            additionalData.append("PI04Q001");
            additionalData.append("CD");
            additionalData.append(String.format("%02d", 4));
            additionalData.append("JOHN");
            additionalData.append("MC");
            additionalData.append(String.format("%02d", tlvData.get("03").length()));
            additionalData.append(tlvData.get("03"));

            acquiringInstitutionId = mPan.substring(0, 8);
        }

        BigDecimal amount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        if (mpm.getTransactionAmount() != null) {
            amount = new BigDecimal(mpm.getTransactionAmount().getValue()).setScale(2, RoundingMode.HALF_EVEN);
        }

        String fee = "";
        if (mpm.getTipOrConvenienceIndicator() != null) {
            fee = "C00000000";
            String feeIndicator = mpm.getTipOrConvenienceIndicator().getValue();
            log.info("Tip Indicator Value : {}", feeIndicator);
            if (feeIndicator.equals("02")) {
                log.info("Getting Fixed Tip Value...");
                String fixedFee = mpm.getValueOfConvenienceFeeFixed().getValue();

                log.info("Tip Value : {}",fixedFee);
                if (!fixedFee.contains(".")) fixedFee = fixedFee.concat(".00");
                BigDecimal feeAmount = new BigDecimal(fixedFee);

                fee = "C" + String.format("%08d", feeAmount.movePointRight(2).intValue());
            } else if (feeIndicator.equals("03")) {
                log.info("Getting Percentage Tip Value...");
                BigDecimal percentageFee = new BigDecimal(mpm.getValueOfConvenienceFeePercentage().getValue());

                log.info("Tip Value : {}", percentageFee);
                fee = percentageFee.toString();
            }
        }
        SimpleDateFormat localMMDD = new SimpleDateFormat("MMdd");
        SimpleDateFormat localHHmmss = new SimpleDateFormat("HHmmss");
        SimpleDateFormat gmtMMddHHmmss = new SimpleDateFormat("MMddHHmmss");
        gmtMMddHHmmss.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date currentDate = new Date();
        TransactionQr transactionQr = new TransactionQr();
        transactionQr.setCPan(cPan);
        transactionQr.setIssuerId("93600844");
        transactionQr.setCategoryCode(mpm.getMerchantCategoryCode().getValue());
        transactionQr.setMerchantId(merchantId);
        transactionQr.setMPan(mPan);
        transactionQr.setAdditionalData(additionalData.toString());
        transactionQr.setAdditionalDataNational(additionalDataNational.toString());
        transactionQr.setAmountFee(fee.isEmpty() ? null : fee);
        transactionQr.setCaptureDate((localMMDD.format(currentDate)));
        transactionQr.setCurrency(currency);
        transactionQr.setApprovalCode(approvalCode);
        transactionQr.setLocalTransactionDate(localMMDD.format(currentDate));
        transactionQr.setLocalTransactionTime(localHHmmss.format(currentDate));
        transactionQr.setPointOfServiceEntryMode("011");
        transactionQr.setProcessingCode(processingCode);

        long epochSecond = Instant.now().getEpochSecond();
        transactionQr.setRrn(String.format("%012d", epochSecond));

        String stan = String.valueOf(epochSecond);
        transactionQr.setStan(stan.substring(stan.length() - 6));

        transactionQr.setTotalAmount(String.valueOf(amount));
        transactionQr.setTransmissionDateTime(gmtMMddHHmmss.format(currentDate));
        transactionQr.setCardAcceptorCountryCode(mpm.getCountryCode().getValue());
        transactionQr.setCardAcceptorTerminalID(
                mpm.getAdditionalDataField() == null || mpm.getAdditionalDataField().getValue().getTerminalLabel() == null ?
                        merchantId : mpm.getAdditionalDataField().getValue().getTerminalLabel().getValue()
        );
        transactionQr.setAcquiringInstitutionId(acquiringInstitutionId);
        transactionQr.setCardAcceptorCity(mpm.getMerchantCity().getValue());
        transactionQr.setCardAcceptorName(mpm.getMerchantName().getValue());
        transactionQr.setAcquirer("Indivara");
        return transactionQr;
    }

    private GeneralRintisRequest qrisRequestBuilder(TransactionQr transactionQr) {
        GeneralRintisRequest result = new GeneralRintisRequest();

        ConsumerData consumerData = new ConsumerData();
        consumerData.setCPan(transactionQr.getCPan());
        consumerData.setIssuerId(transactionQr.getIssuerId());
        result.setConsumerData(consumerData);

        MerchantData merchantData = new MerchantData();
        merchantData.setMPan(transactionQr.getMPan());
        merchantData.setMerchantId(transactionQr.getMerchantId());
        merchantData.setCategoryCode(transactionQr.getCategoryCode());
        result.setMerchantData(merchantData);

        TransactionData transactionData = new TransactionData();
        transactionData.setAdditionalData(transactionQr.getAdditionalData());
        transactionData.setAdditionalDataNational(transactionQr.getAdditionalDataNational());
        transactionData.setAmountFee(transactionQr.getAmountFee());
        transactionData.setApprovalCode(transactionQr.getApprovalCode());
        transactionData.setCaptureDate(transactionQr.getCaptureDate());
        transactionData.setCurrency(transactionQr.getCurrency());
        transactionData.setLocalTransactionDate(transactionQr.getLocalTransactionDate());
        transactionData.setLocalTransactionTime(transactionQr.getLocalTransactionTime());
        transactionData.setPointOfServiceEntryMode(transactionQr.getPointOfServiceEntryMode());
        transactionData.setProcessingCode(transactionQr.getProcessingCode());
        transactionData.setRrn(transactionQr.getRrn());
        transactionData.setStan(transactionQr.getStan());
        transactionData.setTransmissionDateTime(transactionQr.getTransmissionDateTime());
        transactionData.setTotalAmount(new BigDecimal(transactionQr.getTotalAmount()).movePointRight(2).intValueExact());

        CardAcceptorData cardAcceptorData = new CardAcceptorData();
        cardAcceptorData.setCardAcceptorCountryCode(transactionQr.getCardAcceptorCountryCode());
        cardAcceptorData.setCardAcceptorTerminalID(transactionQr.getCardAcceptorTerminalID());
        cardAcceptorData.setCardAcceptorCity(transactionQr.getCardAcceptorCity().length() > 13 ?
                transactionQr.getCardAcceptorCity().substring(0, 13) :
                transactionQr.getCardAcceptorCity());
        cardAcceptorData.setCardAcceptorName(transactionQr.getCardAcceptorName().length()> 25 ?
                transactionQr.getCardAcceptorName().substring(0, 25) :
                transactionQr.getCardAcceptorName());
        transactionData.setCardAcceptorData(cardAcceptorData);

        SenderId senderId = new SenderId();
        senderId.setAcquiringInstitutionId(transactionQr.getMPan().substring(0, 8));
        transactionData.setSenderId(senderId);

        result.setTransactionData(transactionData);

        return result;
    }

    private String validCardNumber(String value) {
        int nCheck = 0;
        boolean bEven = true;
        value = value.replace("/\\D/g", "");

        for (int n = value.length() - 1; n >= 0; n--) {
            char cDigit = value.charAt(n);
            int nDigit = Integer.parseInt(String.valueOf(cDigit), 10);

            if (bEven && (nDigit *= 2) > 9) nDigit -= 9;

            nCheck += nDigit;
            bEven = !bEven;
        }

        return String.valueOf((1000 - nCheck) % 10);
    }

    private HashMap<String, String> parseTlv(String tlvData) {
        HashMap<String, String> result = new HashMap<>();
        int i = 0;
        while (i < tlvData.length()) {
            String tag = tlvData.substring(i, i + 2);
            i += 2;
            int valueLength = Integer.parseInt(tlvData.substring(i, i + 2));
            i += 2;
            String value = tlvData.substring(i, i + valueLength);
            i += valueLength;

            result.put(tag, value);
        }

        return result;
    }
}
