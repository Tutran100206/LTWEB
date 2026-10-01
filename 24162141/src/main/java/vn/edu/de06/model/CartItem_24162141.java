package vn.edu.de06.model;

public class CartItem_24162141 implements java.io.Serializable {
    private String cartItemId;
    private int quantity;
    private double unitPrice;
    private int productId;
    private String cartId;
    private String productName;
    private int stock;
    private int productStatus;
    public String getProductName() { return productName; }
    public void setProductName(String value) { productName=value; }
    public int getStock() { return stock; }
    public void setStock(int value) { stock=value; }
    public int getProductStatus() { return productStatus; }
    public void setProductStatus(int value) { productStatus=value; }
    public java.math.BigDecimal getSubtotal() { return java.math.BigDecimal.valueOf(unitPrice).multiply(java.math.BigDecimal.valueOf(quantity)); }
    public String getCartItemId() { return cartItemId; }
    public void setCartItemId(String value) { this.cartItemId = value; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int value) { this.quantity = value; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double value) { this.unitPrice = value; }
    public int getProductId() { return productId; }
    public void setProductId(int value) { this.productId = value; }
    public String getCartId() { return cartId; }
    public void setCartId(String value) { this.cartId = value; }
}
