package io.roach.retrydemo.domain;

public class OrderRequest {
    private String requestId;

    private Long customerId;

    private String billToFirstName;

    private String billToLastName;

    private Address billAddress;

    private String deliverToFirstName;

    private String deliverToLastName;

    private Address deliveryAddress;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getBillToFirstName() {
        return billToFirstName;
    }

    public void setBillToFirstName(String billToFirstName) {
        this.billToFirstName = billToFirstName;
    }

    public String getBillToLastName() {
        return billToLastName;
    }

    public void setBillToLastName(String billToLastName) {
        this.billToLastName = billToLastName;
    }

    public Address getBillAddress() {
        return billAddress;
    }

    public void setBillAddress(Address billAddress) {
        this.billAddress = billAddress;
    }

    public String getDeliverToFirstName() {
        return deliverToFirstName;
    }

    public void setDeliverToFirstName(String deliverToFirstName) {
        this.deliverToFirstName = deliverToFirstName;
    }

    public String getDeliverToLastName() {
        return deliverToLastName;
    }

    public void setDeliverToLastName(String deliverToLastName) {
        this.deliverToLastName = deliverToLastName;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
