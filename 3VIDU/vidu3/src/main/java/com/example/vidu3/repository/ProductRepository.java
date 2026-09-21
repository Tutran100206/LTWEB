package com.example.vidu3.repository;

import com.example.vidu3.entity.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("select p from Product p join fetch p.user where (:owner is null or p.user.id=:owner) and (lower(p.name) like lower(concat('%', :keyword, '%')) or lower(p.description) like lower(concat('%', :keyword, '%')))")
    Page<Product> search(@Param("keyword") String keyword, @Param("owner") Long owner, Pageable pageable);
    long countByUserId(Long userId);
    List<Product> findByUserId(Long userId);
}
