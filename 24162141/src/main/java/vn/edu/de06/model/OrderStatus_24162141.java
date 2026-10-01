package vn.edu.de06.model;

public enum OrderStatus_24162141 {
    NEW(1,"Đơn hàng mới"), CONFIRMED(2,"Đã xác nhận"), PREPARING(3,"Chuẩn bị hàng"),
    SHIPPING(4,"Vận chuyển"), DELIVERING(5,"Giao hàng"), DELIVERED(6,"Đã giao"),
    CANCELLED(7,"Đơn hàng hủy"), RETURNED(8,"Đơn hàng hoàn");
    private final int code;
    private final String label;
    OrderStatus_24162141(int code,String label) { this.code=code; this.label=label; }
    public int getCode() { return code; }
    public String getLabel() { return label; }
    public static String label(int code) {
        for(OrderStatus_24162141 s:values()) if(s.code==code) return s.label;
        return "Trạng thái chưa xác định";
    }
}
