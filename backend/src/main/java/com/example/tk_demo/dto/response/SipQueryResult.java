package com.example.tk_demo.dto.response;

import java.util.List;

public class SipQueryResult {
    private String retCode;
    private SipInfo sipInfo;
    private List<SipDetail> sipDetail;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public SipInfo getSipInfo() {
        return sipInfo;
    }

    public void setSipInfo(SipInfo sipInfo) {
        this.sipInfo = sipInfo;
    }

    public List<SipDetail> getSipDetail() {
        return sipDetail;
    }

    public void setSipDetail(List<SipDetail> sipDetail) {
        this.sipDetail = sipDetail;
    }
}