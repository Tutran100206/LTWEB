package vn.edu.de06;
import vn.edu.de06.util.*;
import vn.edu.de06.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class SecurityTest_24162141 {
    @Test void passwordFitsExamSchemaAndChecksUnicode() {
        String hash=Password_24162141.hash("MậtKhẩu@123");
        assertEquals(47,hash.length()); assertTrue(Password_24162141.matches("MậtKhẩu@123",hash));
        assertFalse(Password_24162141.matches("wrong",hash)); assertFalse(Password_24162141.matches("wrong","invalid"));
        assertNotEquals(hash,Password_24162141.hash("MậtKhẩu@123"));
    }
    @Test void otpExpiresAtBoundary() {
        PendingRegistration_24162141 p=new PendingRegistration_24162141(new User_24162141(),"012345",1000);
        assertDoesNotThrow(()->p.verify("012345",999));
        assertThrows(IllegalArgumentException.class,()->p.verify("012345",1000));
    }
    @Test void otpLocksAfterFiveWrongAttempts() {
        PendingRegistration_24162141 p=new PendingRegistration_24162141(new User_24162141(),"012345",1000);
        for(int i=0;i<5;i++) assertThrows(IllegalArgumentException.class,()->p.verify("000000",1));
        assertThrows(IllegalArgumentException.class,()->p.verify("012345",2));
    }
    @Test void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class,()->Validation_24162141.id("1 OR 1=1"));
        assertThrows(IllegalArgumentException.class,()->Validation_24162141.id("-1"));
        assertThrows(IllegalArgumentException.class,()->Validation_24162141.email("abc@"));
        assertThrows(IllegalArgumentException.class,()->Validation_24162141.text(" ","Danh mục",200,true));
        assertThrows(IllegalArgumentException.class,()->Validation_24162141.image("javascript:alert(1)"));
    }
}
