package vn.edu.de06.model;

public class Cart_24162141 implements java.io.Serializable {
    private String cartId;
    private int userId;
    private java.sql.Timestamp buyDate;
    private int status;
    private String recipientName, phone, address, paymentMethod;
    private java.util.List<CartItem_24162141> items=new java.util.ArrayList<>();
    public java.util.List<CartItem_24162141> getItems() { return items; }
    public void setItems(java.util.List<CartItem_24162141> value) { items=value; }
    public java.math.BigDecimal getTotal() { return items.stream().map(CartItem_24162141::getSubtotal).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add); }
    public String getStatusLabel() { return OrderStatus_24162141.label(status); }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String value) { recipientName=value; }
    public String getPhone() { return phone; }
    public void setPhone(String value) { phone=value; }
    public String getAddress() { return address; }
    public void setAddress(String value) { address=value; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String value) { paymentMethod=value; }
    public String getCartId() { return cartId; }
    public void setCartId(String value) { this.cartId = value; }
    public int getUserId() { return userId; }
    public void setUserId(int value) { this.userId = value; }
    public java.sql.Timestamp getBuyDate() { return buyDate; }
    public void setBuyDate(java.sql.Timestamp value) { this.buyDate = value; }
    public int getStatus() { return status; }
    public void setStatus(int value) { this.status = value; }
}
