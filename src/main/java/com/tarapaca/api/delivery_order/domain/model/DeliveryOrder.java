package com.tarapaca.api.delivery_order.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tarapaca.api.customers.domain.model.BranchOffice;
import com.tarapaca.api.customers.domain.model.Customer;
import com.tarapaca.api.customers.domain.model.DeliveryZoneBasic;
import com.tarapaca.api.generics.domain.model.GenericProduction;
import com.tarapaca.api.product_order.domain.model.ProductOrder;
import com.tarapaca.api.users.domain.model.UserBasic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class DeliveryOrder extends GenericProduction {
    private Customer customer;
    private BranchOffice branchOffice;
    private DeliveryZoneBasic deliveryZone;
    private List<ProductOrder> productOrders;
    private String generalObservations;

    @Builder.Default
    private BigDecimal totalPrice = new BigDecimal(0);

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant billedAt;
    private UserBasic billedBy;

    @Builder.Default
    @JsonProperty("isBilled")
    private boolean isBilled = false;

    public DeliveryOrder() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        DeliveryOrder that = (DeliveryOrder) o;
        return isBilled == that.isBilled &&
                Objects.equals(customer, that.customer) &&
                Objects.equals(branchOffice, that.branchOffice) &&
                Objects.equals(deliveryZone, that.deliveryZone) &&
                Objects.equals(productOrders, that.productOrders) &&
                Objects.equals(generalObservations, that.generalObservations) &&
                Objects.equals(totalPrice, that.totalPrice) &&
                Objects.equals(billedAt, that.billedAt) &&
                Objects.equals(billedBy, that.billedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), customer, branchOffice, deliveryZone, productOrders,
                generalObservations, totalPrice, billedAt, billedBy, isBilled);
    }
}
