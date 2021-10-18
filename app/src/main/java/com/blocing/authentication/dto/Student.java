package com.blocing.authentication.dto;

import com.google.gson.annotations.SerializedName;
/*
{
"card_did":"did:sov:VcQ4Bge8zcs8JNX7gZ1P1tQBbwtd45CVzrCeu",
"holder_id":"15",
"issuer_id":"1",
"update_date":"2021-09-01 23:59:59"
}
 */
public class Student {
    @SerializedName("card_did")
    private String card_did;
    @SerializedName("holder_id")
    private String holder_id;
    @SerializedName("issuer_id")
    private String issuer_id;
    @SerializedName("update_date")
    private String update_date;

    public String getCard_did() {
        return card_did;
    }

    public void setCard_did(String card_did) {
        this.card_did = card_did;
    }

    public String getHolder_id() {
        return holder_id;
    }

    public void setHolder_id(String holder_id) {
        this.holder_id = holder_id;
    }

    public String getIssuer_id() {
        return issuer_id;
    }

    public void setIssuer_id(String issuer_id) {
        this.issuer_id = issuer_id;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }
}
