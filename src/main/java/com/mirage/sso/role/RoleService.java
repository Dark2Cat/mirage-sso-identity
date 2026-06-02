package com.mirage.sso.role;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mirage.sso.common.BusinessException;
import java.time.LocalDateTime;
import java.util.List;

import com.mirage.sso.permission.PermissionEntity;
import com.mirage.sso.permission.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {
    private final RoleMapper roleMapper;
    private final PermissionService permissionService;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;

    public RoleService(
            RoleMapper roleMapper,
            PermissionService permissionService,
            RolePermissionMapper rolePermissionMapper,
            UserRoleMapper userRoleMapper
    ) {
        this.roleMapper = roleMapper;
        this.permissionService = permissionService;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userRoleMapper = userRoleMapper;
    }

    public List<RoleResponse> listRoles() {
        return roleMapper.selectList(
                        new LambdaQueryWrapper<RoleEntity>().orderByAsc(RoleEntity::getCode)
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RoleEntity getRequired(Long id) {
        RoleEntity entity = roleMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("ROLE_NOT_FOUND", "role not found");
        }
        return entity;
    }

    public List<RoleEntity> rolesByUserId(Long userId) {
        return userRoleMapper.selectRolesByUserId(userId);
    }

    public List<PermissionEntity> permissionsByUserId(Long userId) {
        return userRoleMapper.selectPermissionsByUserId(userId);
    }

    @Transactional
    public RoleResponse create(RoleUpsertRequest request) {
        ensureCodeUnique(request.code(), null);
        LocalDateTime now = LocalDateTime.now();
        RoleEntity entity = new RoleEntity();
        applyRequest(entity, request);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        roleMapper.insert(entity);
        saveRolePermissions(entity.getId(), request.permissions());
        return toResponse(entity);
    }

    @Transactional
    public RoleResponse update(Long id, RoleUpsertRequest request) {
        RoleEntity entity = getRequired(id);
        ensureCodeUnique(request.code(), id);
        applyRequest(entity, request);
        entity.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(entity);
        if (request.permissions() != null) {
            saveRolePermissions(id, request.permissions());
        }
        return toResponse(entity);
    }

    @Transactional
    public RoleResponse updateStatus(Long id, RoleStatus status) {
        RoleEntity entity = getRequired(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(entity);
        return toResponse(entity);
    }

    @Transactional
    public RoleResponse updatePermissions(Long id, List<String> permissions) {
        RoleEntity entity = getRequired(id);
        saveRolePermissions(id, permissions);
        return toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        getRequired(id);
        rolePermissionMapper.deleteByRoleId(id);
        roleMapper.deleteById(id);
    }

    public void saveUserRoles(Long userId, List<String> roleCodes) {
        userRoleMapper.deleteByUserId(userId);
        if (roleCodes == null || roleCodes.isEmpty()) {
            return;
        }
        List<RoleEntity> roles = roleMapper.selectList(new LambdaQueryWrapper<RoleEntity>().in(RoleEntity::getCode, roleCodes));
        if (roles.size() != roleCodes.size()) {
            throw new BusinessException("ROLE_NOT_FOUND", "one or more roles not found");
        }
        LocalDateTime now = LocalDateTime.now();
        roles.forEach(role -> userRoleMapper.insertUserRole(userId, role.getId(), now));
    }

    private RoleResponse toResponse(RoleEntity entity) {
        List<String> permissions = rolePermissionMapper.selectPermissionsByRoleId(entity.getId())
                .stream()
                .map(PermissionEntity::getCode)
                .toList();
        return RoleResponse.from(entity, rolePermissionMapper.countUsersByRoleId(entity.getId()), permissions);
    }

    private void saveRolePermissions(Long roleId, List<String> permissionCodes) {
        rolePermissionMapper.deleteByRoleId(roleId);
        List<PermissionEntity> permissions = permissionService.findByCodes(permissionCodes);
        if (permissionCodes != null && permissions.size() != permissionCodes.size()) {
            throw new BusinessException("PERMISSION_NOT_FOUND", "one or more permissions not found");
        }
        LocalDateTime now = LocalDateTime.now();
        permissions.forEach(permission -> rolePermissionMapper.insertRolePermission(roleId, permission.getId(), now));
    }

    private void applyRequest(RoleEntity entity, RoleUpsertRequest request) {
        entity.setCode(request.code());
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setDataScope(request.dataScope() == null || request.dataScope().isBlank() ? "本人" : request.dataScope());
        entity.setStatus(request.status());
    }

    private void ensureCodeUnique(String code, Long selfId) {
        RoleEntity existing = roleMapper.selectOne(
                new LambdaQueryWrapper<RoleEntity>()
                        .eq(RoleEntity::getCode, code)
                        .last("limit 1")
        );
        if (existing != null && !existing.getId().equals(selfId)) {
            throw new BusinessException("ROLE_CODE_EXISTS", "role code already exists");
        }
    }
}
