package com.example.vidu3.service;
import com.example.vidu3.entity.*;
import com.example.vidu3.repository.*;
import com.example.vidu3.mapper.ProductMapperImpl;
import com.example.vidu3.dto.ProductDTO;
import com.example.vidu3.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ProductServiceTest {
    ProductRepository products; UserRepository users; CloudinaryService cloud; ProductService service; Product product; CustomUserDetails actor;
    @BeforeEach void setup() {
        products=mock(ProductRepository.class); users=mock(UserRepository.class); cloud=mock(CloudinaryService.class);
        service=new ProductService(products,users,new ProductMapperImpl(),cloud,new ImageLifecycle(cloud));
        var role=new Role(); role.setName("USER"); var owner=new User(); owner.setId(1L); owner.setUsername("owner"); owner.setRole(role); actor=new CustomUserDetails(owner);
        product=new Product(); product.setId(5L); product.setUser(owner); product.setImagePublicId("old-image"); when(products.findById(5L)).thenReturn(Optional.of(product)); when(users.findById(1L)).thenReturn(Optional.of(owner));
    }
    @AfterEach void cleanup() { if(TransactionSynchronizationManager.isSynchronizationActive()) TransactionSynchronizationManager.clearSynchronization(); }
    @Test void foreignOwnerCannotReadUpdateOrDelete() {
        var stranger=new User(); stranger.setId(99L); product.setUser(stranger);
        assertThrows(AccessDeniedException.class,()->service.get(5L,actor));
        assertThrows(AccessDeniedException.class,()->service.save(5L,new ProductDTO(),null,actor));
        assertThrows(AccessDeniedException.class,()->service.delete(5L,actor)); verifyNoInteractions(cloud); verify(products,never()).delete(any());
    }
    @Test void submittedOwnerCannotTransferNormalUsersProduct() {
        var form=new ProductDTO(); form.setUserId(99L); form.setName("Mới"); service.save(5L,form,null,actor);
        assertEquals(1L,product.getUser().getId()); assertEquals("old-image",product.getImagePublicId());
    }
    @Test void replacedImageIsDeletedOnlyAfterCommit() {
        TransactionSynchronizationManager.initSynchronization();
        when(cloud.upload(null)).thenReturn(new CloudinaryUploadResult("https://example.com/new.png","new-image"));
        service.save(5L,new ProductDTO(),null,actor); verify(cloud,never()).delete(anyString());
        for(var callback:TransactionSynchronizationManager.getSynchronizations()) callback.afterCommit();
        verify(cloud).delete("old-image"); assertEquals("new-image",product.getImagePublicId());
    }
    @Test void failedDatabaseSaveCleansUpNewUpload() {
        TransactionSynchronizationManager.initSynchronization();
        when(cloud.upload(null)).thenReturn(new CloudinaryUploadResult("https://example.com/new.png","new-image"));
        when(products.saveAndFlush(any())).thenThrow(new IllegalStateException("Database unavailable"));
        assertThrows(IllegalStateException.class,()->service.save(5L,new ProductDTO(),null,actor));
        for(var callback:TransactionSynchronizationManager.getSynchronizations()) callback.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
        verify(cloud).delete("new-image"); verify(cloud,never()).delete("old-image");
    }
    @Test void adminCanManageOtherOwnersProduct() {
        var role=new Role(); role.setName("ADMIN"); var admin=new User(); admin.setId(99L); admin.setRole(role);
        assertEquals(5L,service.get(5L,new CustomUserDetails(admin)).getId());
    }
}
