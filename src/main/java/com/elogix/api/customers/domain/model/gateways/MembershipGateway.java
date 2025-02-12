package com.elogix.api.customers.domain.model.gateways;

import com.elogix.api.customers.domain.model.EMembership;
import com.elogix.api.customers.domain.model.Membership;
import com.elogix.api.generics.domain.gateway.GenericGateway;

public interface MembershipGateway extends GenericGateway<Membership> {
    Membership deleteByName(EMembership name);

    Membership findByName(EMembership name, boolean isDeleted);
}
