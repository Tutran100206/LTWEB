package vn.edu.de06;

import org.junit.jupiter.api.Test;
import vn.edu.de06.service.CartService_24162141;
import vn.edu.de06.model.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CartTest_24162141 {
    @Test void rejectsInvalidQuantitiesAndFilters() {
        for(String value:new String[]{null,"","0","-1","1.5","2147483648"})
            assertThrows(IllegalArgumentException.class,()->CartService_24162141.positive(value));
        assertNull(CartService_24162141.status(""));
        assertThrows(IllegalArgumentException.class,()->CartService_24162141.status("9"));
        for(int n=1;n<=8;n++) assertEquals(n,CartService_24162141.status(String.valueOf(n)));
    }
    @Test void totalsAvoidBinaryFloatingPointAccumulation() {
        CartItem_24162141 i=new CartItem_24162141(); i.setUnitPrice(0.1); i.setQuantity(3);
        Cart_24162141 c=new Cart_24162141(); c.getItems().add(i);
        assertEquals(0,new BigDecimal("0.3").compareTo(c.getTotal()));
    }
    @Test void requiredShippingFieldsAreBounded() {
        assertThrows(IllegalArgumentException.class,()->CartService_24162141.text("  ",100,"Tên"));
        assertThrows(IllegalArgumentException.class,()->CartService_24162141.text("x".repeat(501),500,"Địa chỉ"));
        assertEquals("An",CartService_24162141.text(" An ",100,"Tên"));
    }
}
