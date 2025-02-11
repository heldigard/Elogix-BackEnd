package com.tarapaca.api.generics.infrastructure.repository.GenericEntity;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.tarapaca.api.generics.domain.gateway.GenericGateway;
import com.tarapaca.api.generics.domain.model.GenericEntity;
import com.tarapaca.api.generics.infrastructure.helpers.GenericMapperGateway;
import com.tarapaca.api.generics.infrastructure.repository.GenericBasic.GenericBasicGatewayImpl;
import com.tarapaca.api.shared.infraestructure.helpers.UpdateUtils;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract implementation of the GenericGateway interface providing common CRUD
 * operations and auditing functionality.
 * This class serves as a base implementation for entity gateways in the
 * application.
 *
 * @param <T> The domain entity type extending GenericEntity
 * @param <D> The data entity type extending GenericEntityData and implementing
 *            Serializable
 * @param <R> The repository type extending both JpaRepository and
 *            GenericEntityRepository
 * @param <M> The mapper type extending GenericMapperGateway
 *            <p>
 *            Features:
 *            - CRUD operations (create, read, update, delete)
 *            - Soft delete functionality
 *            - Auditing support (created/updated/deleted by/at)
 *            - Pagination support
 *            - Sorting capabilities
 *            - Filter management for deleted entities
 *            <p>
 *            The class implements automatic auditing through AuditorAwareImpl
 *            and uses JPA/Hibernate for persistence.
 *            It provides both soft and hard delete capabilities, with soft
 *            delete being the default behavior.
 * @see GenericEntity
 * @see GenericEntityData
 * @see GenericEntityRepository
 * @see GenericMapperGateway
 * @see GenericGateway
 */
@Slf4j
public abstract class GenericGatewayImpl<T extends GenericEntity, D extends GenericEntityData, R extends JpaRepository<D, Long> & GenericEntityRepository<D>, M extends GenericMapperGateway<T, D>>
        extends GenericBasicGatewayImpl<T, D, R, M>
        implements GenericGateway<T> {

    protected final UpdateUtils updateUtils;

    protected GenericGatewayImpl(
            R repository,
            M mapper,
            EntityManager entityManager,
            UpdateUtils updateUtils,
            String deletedFilter) {
        super(repository, mapper, entityManager, deletedFilter);
        this.updateUtils = updateUtils;
    }

    // CRUD Operations
    @Override
    @Transactional
    public T save(T entity) {
        D entityData = mapper.toData(entity);
        D savedData = repository.save(entityData);

        return mapper.toDomain(savedData);
    }

    @Override
    @Transactional
    public List<T> saveAll(List<T> entityList) {
        if (entityList == null) {
            throw new IllegalArgumentException("Entity list cannot be null");
        }
        List<D> entityDataList = mapper.toData(entityList).stream().toList();
        List<D> savedDataList = repository.saveAll(entityDataList);

        return mapper.toDomain(savedDataList).stream().toList();
    }

    @Override
    @Transactional
    public T add(T entity) {
        D entityData = mapper.toData(entity);
        entityData.setId((long) -1);
        D savedData = repository.save(entityData);

        return Optional.of(savedData)
                .filter(saved -> saved.getId() != null)
                .map(mapper::toDomain)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public T update(T entity) {
        T existing = findById(entity.getId(), false);
        preserveExistingData(entity, existing);
        D entityData = mapper.toData(entity);
        D savedData = repository.save(entityData);

        return Optional.of(savedData)
                .filter(saved -> saved.getId() != null)
                .map(mapper::toDomain)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void preserveExistingData(T newEntity, T existing) {
        if (newEntity.getCreatedAt() == null)
            newEntity.setCreatedAt(existing.getCreatedAt());
        if (newEntity.getCreatedBy() == null)
            newEntity.setCreatedBy(existing.getCreatedBy());

        if (newEntity.getUpdatedAt() == null)
            newEntity.setUpdatedAt(existing.getUpdatedAt());
        if (newEntity.getUpdatedBy() == null)
            newEntity.setUpdatedBy(existing.getUpdatedBy());

        if (newEntity.getDeletedAt() == null)
            newEntity.setDeletedAt(existing.getDeletedAt());
        if (newEntity.getDeletedBy() == null)
            newEntity.setDeletedBy(existing.getDeletedBy());
    }

    @Override
    @Transactional
    public List<T> deleteAll(List<T> entityList) {
        entityList.forEach(entity -> {
            entity.setDeletedBy(updateUtils.getCurrentUser());
            entity.setDeletedAt(Instant.now());
            entity.setDeleted(true);
        });
        return saveAll(entityList);
    }

    // Delete Operations
    @Override
    @Transactional
    public T delete(T entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        entity.setDeletedBy(updateUtils.getCurrentUser());
        entity.setDeletedAt(Instant.now());
        entity.setDeleted(true);
        return save(entity);
    }

    @Override
    @Transactional
    public T deleteById(Long id) {
        T entity = findById(id, false);
        return delete(entity);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        repository.hardDeleteById(id);
    }
}
