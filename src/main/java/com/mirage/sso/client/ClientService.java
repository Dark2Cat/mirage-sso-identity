package com.mirage.sso.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mirage.sso.common.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppAccessMapper appAccessMapper;

    public ClientService(ClientMapper clientMapper, PasswordEncoder passwordEncoder, AppAccessMapper appAccessMapper) {
        this.clientMapper = clientMapper;
        this.passwordEncoder = passwordEncoder;
        this.appAccessMapper = appAccessMapper;
    }

    public List<ClientSummary> listClients() {
        return clientMapper.selectList(
                        new LambdaQueryWrapper<ClientEntity>()
                                .orderByDesc(ClientEntity::getCreatedAt)
                )
                .stream()
                .map(ClientSummary::from)
                .toList();
    }

    @Transactional
    public ClientSummary create(ClientUpsertRequest request) {
        ensureClientIdUnique(request.clientId(), null);
        LocalDateTime now = LocalDateTime.now();
        ClientEntity entity = new ClientEntity();
        applyRequest(entity, request);
        if (request.clientSecret() != null && !request.clientSecret().isBlank()) {
            entity.setClientSecretHash(passwordEncoder.encode(request.clientSecret()));
        }
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        clientMapper.insert(entity);
        return ClientSummary.from(entity);
    }

    @Transactional
    public ClientSummary update(Long id, ClientUpsertRequest request) {
        ClientEntity entity = getRequired(id);
        ensureClientIdUnique(request.clientId(), id);
        applyRequest(entity, request);
        if (request.clientSecret() != null && !request.clientSecret().isBlank()) {
            entity.setClientSecretHash(passwordEncoder.encode(request.clientSecret()));
        }
        entity.setUpdatedAt(LocalDateTime.now());
        clientMapper.updateById(entity);
        return ClientSummary.from(entity);
    }

    @Transactional
    public ClientSummary updateStatus(Long id, ClientStatus status) {
        ClientEntity entity = getRequired(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        clientMapper.updateById(entity);
        return ClientSummary.from(entity);
    }

    @Transactional
    public ClientSecretResponse resetSecret(Long id) {
        ClientEntity entity = getRequired(id);
        if (entity.getClientType() == ClientType.SPA) {
            throw new BusinessException("PUBLIC_CLIENT", "public client does not use client secret");
        }
        String secret = entity.getClientId() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        entity.setClientSecretHash(passwordEncoder.encode(secret));
        entity.setUpdatedAt(LocalDateTime.now());
        clientMapper.updateById(entity);
        return new ClientSecretResponse(secret);
    }

    @Transactional
    public void delete(Long id) {
        getRequired(id);
        clientMapper.deleteById(id);
    }

    @Transactional
    public AppAccessResponse assignAccess(AppAccessRequest request) {
        if (request.userId() == null && (request.roleCode() == null || request.roleCode().isBlank())) {
            throw new BusinessException("ASSIGNMENT_TARGET_REQUIRED", "userId or roleCode is required");
        }
        if (request.userId() != null) {
            appAccessMapper.deleteByUserId(request.userId());
            LocalDateTime now = LocalDateTime.now();
            request.appCodes().forEach(appCode -> appAccessMapper.insertUserApp(request.userId(), appCode, now));
            return new AppAccessResponse(request.userId(), null, appAccessMapper.selectUserApps(request.userId()));
        }
        return new AppAccessResponse(null, request.roleCode(), request.appCodes());
    }

    private ClientEntity getRequired(Long id) {
        ClientEntity entity = clientMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("CLIENT_NOT_FOUND", "client not found");
        }
        return entity;
    }

    private void applyRequest(ClientEntity entity, ClientUpsertRequest request) {
        entity.setClientId(request.clientId());
        entity.setClientName(request.clientName());
        entity.setClientType(request.clientType());
        entity.setRedirectUris(request.redirectUris());
        entity.setPostLogoutRedirectUris(request.postLogoutRedirectUris());
        entity.setGrantTypes(request.grantTypes());
        entity.setScopes(request.scopes());
        entity.setAccessTokenTtl(request.accessTokenTtl());
        entity.setRefreshTokenTtl(request.refreshTokenTtl());
        entity.setRequireConsent(request.requireConsent() == null || request.requireConsent());
        entity.setStatus(request.status());
    }

    private void ensureClientIdUnique(String clientId, Long selfId) {
        ClientEntity existing = clientMapper.selectOne(
                new LambdaQueryWrapper<ClientEntity>()
                        .eq(ClientEntity::getClientId, clientId)
                        .last("limit 1")
        );
        if (existing != null && !existing.getId().equals(selfId)) {
            throw new BusinessException("CLIENT_ID_EXISTS", "client id already exists");
        }
    }
}
