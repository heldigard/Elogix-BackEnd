package com.elogix.api.customers.infrastructure.driven_adapters.jpa_repository.membership;

import java.util.Optional;

import org.hibernate.Session;

import com.elogix.api.customers.domain.model.EMembership;
import com.elogix.api.customers.domain.model.Membership;
import com.elogix.api.customers.domain.model.gateways.MembershipGateway;
import com.elogix.api.customers.infrastructure.helpers.mappers.MembershipMapper;
import com.elogix.api.generics.infrastructure.repository.GenericEntity.GenericGatewayImpl;
import com.elogix.api.shared.infraestructure.helpers.UpdateUtils;

import jakarta.persistence.EntityManager;

public class MembershipGatewayImpl
        extends GenericGatewayImpl<Membership, MembershipData, MembershipDataJpaRepository, MembershipMapper>
        implements MembershipGateway {

    public MembershipGatewayImpl(
            MembershipDataJpaRepository repository,
            MembershipMapper mapper,
            EntityManager entityManager,
            UpdateUtils updateUtils,
            String deletedFilter) {
        super(repository, mapper, entityManager, updateUtils, deletedFilter);
    }

    @Override
    public Membership deleteByName(EMembership name) {
        Membership membership = findByName(name, false);
        return delete(membership);
    }

    @Override
    public Membership findByName(EMembership name, boolean isDeleted) {
        Session session = setDeleteFilter(isDeleted);
        Optional<MembershipData> membershipData = repository.findByName(name);
        session.disableFilter(this.deletedFilter);

        return mapper.toDomain(membershipData.orElse(null));
    }
}
