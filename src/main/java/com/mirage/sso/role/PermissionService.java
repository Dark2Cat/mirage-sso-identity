package com.mirage.sso.role;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mirage.sso.common.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {
    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    public List<PermissionResponse> listPermissions(PermissionType type) {
        LambdaQueryWrapper<PermissionEntity> query = new LambdaQueryWrapper<PermissionEntity>()
                .orderByAsc(PermissionEntity::getSortOrder)
                .orderByAsc(PermissionEntity::getCode);
        if (type != null) {
            query.eq(PermissionEntity::getType, type);
        }
        return permissionMapper.selectList(query)
                .stream()
                .map(PermissionResponse::from)
                .toList();
    }

    public PermissionEntity getRequired(Long id) {
        PermissionEntity entity = permissionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("PERMISSION_NOT_FOUND", "permission not found");
        }
        return entity;
    }

    public List<PermissionEntity> findByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return permissionMapper.selectList(new LambdaQueryWrapper<PermissionEntity>().in(PermissionEntity::getCode, codes));
    }

    @Transactional
    public PermissionResponse create(PermissionUpsertRequest request) {
        ensureCodeUnique(request.code(), null);
        LocalDateTime now = LocalDateTime.now();
        PermissionEntity entity = new PermissionEntity();
        applyRequest(entity, request);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        permissionMapper.insert(entity);
        return PermissionResponse.from(entity);
    }

    @Transactional
    public PermissionResponse update(Long id, PermissionUpsertRequest request) {
        PermissionEntity entity = getRequired(id);
        ensureCodeUnique(request.code(), id);
        applyRequest(entity, request);
        entity.setUpdatedAt(LocalDateTime.now());
        permissionMapper.updateById(entity);
        return PermissionResponse.from(entity);
    }

    @Transactional
    public PermissionResponse updateStatus(Long id, RoleStatus status) {
        PermissionEntity entity = getRequired(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        permissionMapper.updateById(entity);
        return PermissionResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        getRequired(id);
        permissionMapper.deleteById(id);
    }

    private void applyRequest(PermissionEntity entity, PermissionUpsertRequest request) {
        entity.setCode(request.code());
        entity.setName(request.name());
        entity.setType(request.type());
        entity.setParentId(request.parentId());
        entity.setResource(request.resource());
        entity.setDescription(request.description());
        entity.setSortOrder(request.sortOrder());
        entity.setStatus(request.status());
    }

    private void ensureCodeUnique(String code, Long selfId) {
        PermissionEntity existing = permissionMapper.selectOne(
                new LambdaQueryWrapper<PermissionEntity>()
                        .eq(PermissionEntity::getCode, code)
                        .last("limit 1")
        );
        if (existing != null && !existing.getId().equals(selfId)) {
            throw new BusinessException("PERMISSION_CODE_EXISTS", "permission code already exists");
        }
    }
}
