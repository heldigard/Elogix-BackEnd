package com.elogix.api.product.infrastructure.repository.product_price;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.elogix.api.generics.infrastructure.repository.GenericEntity.GenericEntityRepository;

@Repository
public interface ProductPriceDataJpaRepository
        extends GenericEntityRepository<ProductPriceData> {
    void deleteByProductRef(String productRef);

    Optional<ProductPriceData> findByProductRef(String productRef);
}
