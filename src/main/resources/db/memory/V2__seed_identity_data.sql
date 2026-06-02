insert into sso_user (
    id, username, email, phone, organization_id, password_hash, nickname, avatar_url, status,
    last_login_at, created_at, updated_at, deleted_at
) values (
    1001,
    'admin',
    'admin@mirage.local',
    null,
    7002,
    '{noop}mirage@2026',
    'Mirage Admin',
    null,
    'ACTIVE',
    current_timestamp,
    current_timestamp,
    current_timestamp,
    null
);

insert into sso_organization (
    id, parent_id, name, code, manager, user_count, sort_order, status, created_at, updated_at
) values
    (7001, null, 'Mirage 总部', 'HQ', 'Mirage Admin', 16, 1, 'ACTIVE', current_timestamp, current_timestamp),
    (7002, 7001, '平台研发部', 'PLATFORM', 'Mirage Admin', 8, 10, 'ACTIVE', current_timestamp, current_timestamp),
    (7003, 7002, '身份认证组', 'IAM', 'admin', 4, 11, 'ACTIVE', current_timestamp, current_timestamp),
    (7004, 7002, '权限治理组', 'RBAC', 'operator', 4, 12, 'ACTIVE', current_timestamp, current_timestamp),
    (7005, 7001, '运营体验组', 'OPS', 'operator', 5, 20, 'ACTIVE', current_timestamp, current_timestamp),
    (7006, 7001, '外部协作', 'EXT', 'guest-owner', 3, 30, 'DISABLED', current_timestamp, current_timestamp);

insert into sso_role (id, code, name, description, data_scope, status, created_at, updated_at) values
    (2001, 'ADMIN', '管理员', '认证中心管理员', '全部组织', 'ACTIVE', current_timestamp, current_timestamp),
    (2002, 'USER', '普通用户', '默认注册用户', '本人', 'ACTIVE', current_timestamp, current_timestamp);

insert into sso_permission (
    id, code, name, type, parent_id, resource, description, sort_order, status, created_at, updated_at
) values
    (3001, 'sso:user:read', '用户读取', 'API', null, '/api/admin/users', '查看用户列表', 10, 'ACTIVE', current_timestamp, current_timestamp),
    (3002, 'sso:client:manage', '客户端管理', 'API', null, '/api/admin/clients', '管理 OAuth2 客户端', 20, 'ACTIVE', current_timestamp, current_timestamp),
    (3003, 'sso:audit:read', '审计读取', 'API', null, '/api/admin/audit-logs', '查看审计日志', 30, 'ACTIVE', current_timestamp, current_timestamp),
    (3004, 'app:portal:access', '门户访问', 'APP', null, '/api/portal/apps', '访问应用门户', 40, 'ACTIVE', current_timestamp, current_timestamp),
    (3005, 'sso:user:manage', '用户管理', 'ACTION', 3001, '用户创建/编辑/禁用/重置密码', '维护用户资料、状态和密码', 15, 'ACTIVE', current_timestamp, current_timestamp),
    (3006, 'sso:role:manage', '角色管理', 'MENU', null, '/admin/roles', '维护角色和角色授权', 35, 'ACTIVE', current_timestamp, current_timestamp),
    (3007, 'sso:permission:manage', '权限管理', 'MENU', null, '/admin/permissions', '维护权限资源定义', 36, 'ACTIVE', current_timestamp, current_timestamp),
    (3008, 'sso:org:manage', '组织管理', 'MENU', null, '/admin/organizations', '维护组织部门结构', 37, 'ACTIVE', current_timestamp, current_timestamp),
    (3101, 'app:oa:access', 'Mirage OA 访问', 'APP', 3004, 'oa', '访问 Mirage OA', 101, 'ACTIVE', current_timestamp, current_timestamp),
    (3102, 'app:crm:access', 'Mirage CRM 访问', 'APP', 3004, 'crm', '访问 Mirage CRM', 102, 'ACTIVE', current_timestamp, current_timestamp),
    (3103, 'app:mall:access', 'Mirage Mall 访问', 'APP', 3004, 'mall', '访问 Mirage Mall', 103, 'ACTIVE', current_timestamp, current_timestamp),
    (3104, 'app:ai-lab:access', 'AI Workbench 访问', 'APP', 3004, 'ai-lab', '访问 AI Workbench', 104, 'ACTIVE', current_timestamp, current_timestamp);

insert into sso_user_role (user_id, role_id, created_at) values
    (1001, 2001, current_timestamp),
    (1001, 2002, current_timestamp);

insert into sso_role_permission (role_id, permission_id, created_at) values
    (2001, 3001, current_timestamp),
    (2001, 3002, current_timestamp),
    (2001, 3003, current_timestamp),
    (2001, 3004, current_timestamp),
    (2001, 3005, current_timestamp),
    (2001, 3006, current_timestamp),
    (2001, 3007, current_timestamp),
    (2001, 3008, current_timestamp),
    (2001, 3101, current_timestamp),
    (2001, 3102, current_timestamp),
    (2001, 3103, current_timestamp),
    (2001, 3104, current_timestamp),
    (2002, 3101, current_timestamp),
    (2002, 3102, current_timestamp);

insert into sso_client (
    id, client_id, client_secret_hash, client_name, client_type, redirect_uris,
    post_logout_redirect_uris, grant_types, scopes, access_token_ttl, refresh_token_ttl,
    require_consent, status, created_at, updated_at
) values
    (
        4001,
        'mirage-sso-front',
        null,
        'Mirage SSO Frontend',
        'SPA',
        '["http://127.0.0.1:5173/oauth/callback"]',
        '["http://127.0.0.1:5173/login"]',
        '["authorization_code", "refresh_token"]',
        '["openid", "profile", "email", "app:portal:access"]',
        900,
        604800,
        true,
        'ENABLED',
        current_timestamp,
        current_timestamp
    ),
    (
        4002,
        'mirage-oa',
        '{noop}mirage-oa-secret',
        'Mirage OA',
        'WEB',
        '["http://127.0.0.1:5174/login/oauth2/code/mirage"]',
        '["http://127.0.0.1:5174/logout"]',
        '["authorization_code", "refresh_token"]',
        '["openid", "profile", "oa:approval"]',
        900,
        604800,
        true,
        'ENABLED',
        current_timestamp,
        current_timestamp
    );

insert into sso_app (
    id, client_id, code, name, description, entry_url, logo_url, category, tech_tags,
    visibility, sort_order, status, created_at, updated_at
) values
    (5001, 'mirage-oa', 'oa', 'Mirage OA', '审批、组织、考勤和流程协作的内部办公系统。', 'http://127.0.0.1:5174', null, '业务系统', '["Spring Boot", "Workflow", "RBAC"]', 'LOGIN', 10, 'ENABLED', current_timestamp, current_timestamp),
    (5002, 'mirage-crm', 'crm', 'Mirage CRM', '客户、商机、联系人和销售跟进的客户经营台。', 'http://127.0.0.1:5175', null, '业务系统', '["React", "MySQL", "Pipeline"]', 'LOGIN', 20, 'ENABLED', current_timestamp, current_timestamp),
    (5003, 'mirage-mall', 'mall', 'Mirage Mall', '商品、订单、会员、营销和交易链路的商城样板。', 'http://127.0.0.1:5176', null, '交易系统', '["Order", "Payment", "Inventory"]', 'AUTHORIZED', 30, 'DRAFT', current_timestamp, current_timestamp),
    (5004, 'mirage-ai', 'ai-lab', 'AI Workbench', '知识库问答、内容生成和 Agent 工具调用实验室。', 'http://127.0.0.1:5177', null, 'AI 应用', '["LLM", "RAG", "Agent"]', 'LOGIN', 40, 'ENABLED', current_timestamp, current_timestamp);

insert into sso_audit_log (
    id, actor_id, actor_name, event_type, target_type, target_id, result,
    failure_reason, ip_address, user_agent, request_id, metadata, created_at
) values
    (6001, 1001, 'admin', 'LOGIN_SUCCESS', 'USER', '1001', 'SUCCESS', null, '127.0.0.1', 'seed', 'seed-001', '{"source":"seed"}', current_timestamp),
    (6002, 1001, 'admin', 'CLIENT_CREATED', 'CLIENT', 'mirage-sso-front', 'SUCCESS', null, '127.0.0.1', 'seed', 'seed-002', '{"source":"seed"}', current_timestamp);
