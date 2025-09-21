package com.java.moveminds.dto.requests;

import lombok.Data;

@Data
public class PayHereNotifyRequest {
    
    private String merchant_id;
    private String order_id;
    private String payment_id;
    private String payhere_amount;
    private String payhere_currency;
    private String status_code;
    private String md5sig;
    private String method;
    private String status_message;
    private String card_holder_name;
    private String card_no;
    private String card_expiry;
}
