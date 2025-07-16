package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.Product;
import org.allen.erpoor.inventory.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p.productId AS productId, p.name AS name FROM Product p WHERE p.enabled = true")
    List<ProductOption> findAllProductOptions();

    List<Product> findByEnabled(boolean enabled);

}

