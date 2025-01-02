package com.eazywrite.app.data.model;

public class VerifyBean {

    /**
     * codeType
     */
    private String codeType;
    /**
     * graphicCode
     */
    private String graphicCode;
    /**
     * phoneOrEmail
     */
    private String phoneOrEmail;

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getGraphicCode() {
        return graphicCode;
    }

    public void setGraphicCode(String graphicCode) {
        this.graphicCode = graphicCode;
    }

    public String getPhoneOrEmail() {
        return phoneOrEmail;
    }

    public void setPhoneOrEmail(String phoneOrEmail) {
        this.phoneOrEmail = phoneOrEmail;
    }
}
