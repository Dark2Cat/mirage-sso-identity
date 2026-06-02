package com.mirage.sso.organization;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mirage.sso.common.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {
    private final OrganizationMapper organizationMapper;

    public OrganizationService(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    public List<OrganizationResponse> listOrganizations() {
        return organizationMapper.selectList(
                        new LambdaQueryWrapper<OrganizationEntity>()
                                .orderByAsc(OrganizationEntity::getSortOrder)
                                .orderByAsc(OrganizationEntity::getCreatedAt)
                )
                .stream()
                .map(OrganizationResponse::from)
                .toList();
    }

    public OrganizationEntity getRequired(Long id) {
        OrganizationEntity entity = organizationMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("ORGANIZATION_NOT_FOUND", "organization not found");
        }
        return entity;
    }

    @Transactional
    public OrganizationResponse create(OrganizationUpsertRequest request) {
        ensureCodeUnique(request.code(), null);
        LocalDateTime now = LocalDateTime.now();
        OrganizationEntity entity = new OrganizationEntity();
        applyRequest(entity, request);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        organizationMapper.insert(entity);
        return OrganizationResponse.from(entity);
    }

    @Transactional
    public OrganizationResponse update(Long id, OrganizationUpsertRequest request) {
        OrganizationEntity entity = getRequired(id);
        ensureCodeUnique(request.code(), id);
        applyRequest(entity, request);
        entity.setUpdatedAt(LocalDateTime.now());
        organizationMapper.updateById(entity);
        return OrganizationResponse.from(entity);
    }

    @Transactional
    public OrganizationResponse updateStatus(Long id, OrganizationStatus status) {
        OrganizationEntity entity = getRequired(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        organizationMapper.updateById(entity);
        return OrganizationResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        getRequired(id);
        Long children = organizationMapper.selectCount(
                new LambdaQueryWrapper<OrganizationEntity>().eq(OrganizationEntity::getParentId, id)
        );
        if (children > 0) {
            throw new BusinessException("ORGANIZATION_HAS_CHILDREN", "organization has child nodes");
        }
        organizationMapper.deleteById(id);
    }

    private void applyRequest(OrganizationEntity entity, OrganizationUpsertRequest request) {
        entity.setParentId(request.parentId());
        entity.setName(request.name());
        entity.setCode(request.code());
        entity.setManager(request.manager());
        entity.setUserCount(request.userCount());
        entity.setSortOrder(request.sortOrder());
        entity.setStatus(request.status());
    }

    private void ensureCodeUnique(String code, Long selfId) {
        OrganizationEntity existing = organizationMapper.selectOne(
                new LambdaQueryWrapper<OrganizationEntity>()
                        .eq(OrganizationEntity::getCode, code)
                        .last("limit 1")
        );
        if (existing != null && !existing.getId().equals(selfId)) {
            throw new BusinessException("ORGANIZATION_CODE_EXISTS", "organization code already exists");
        }
    }
}
