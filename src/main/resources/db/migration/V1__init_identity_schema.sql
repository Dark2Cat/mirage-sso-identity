create table sso_user (
    -- 用户主键 ID
    id bigint not null primary key,
    -- 用户名，唯一，登录账号
    username varchar(64) not null,
    -- 邮箱，可空，支持找回密码和通知
    email varchar(128) null,
    -- 手机号，可空，唯一
    phone varchar(32) null,
    -- 用户所属组织 ID，可空
    organization_id bigint null,
    -- 密码哈希，使用 PasswordEncoder 存储
    password_hash varchar(255) not null,
    -- 昵称，展示用
    nickname varchar(64) null,
    -- 头像地址，支持 URL 或 data URL
    avatar_url varchar(512) null,
    -- 用户状态：PENDING-待验证，ACTIVE-正常，DISABLED-已禁用，LOCKED-已锁定，DELETED-已删除
    status varchar(32) not null,
    -- 最近登录时间
    last_login_at datetime(3) null,
    -- 创建时间
    created_at datetime(3) not null,
    -- 更新时间
    updated_at datetime(3) not null,
    -- 逻辑删除时间
    deleted_at datetime(3) null,
    unique key uk_sso_user_username (username),
    unique key uk_sso_user_email (email),
    unique key uk_sso_user_phone (phone)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table sso_role (
    -- 角色主键 ID
    id bigint not null primary key,
    -- 角色编码，唯一，例如 ADMIN、USER
    code varchar(64) not null,
    -- 角色名称
    name varchar(64) not null,
    -- 角色描述
    description varchar(255) null,
    -- 数据范围：全部组织、本部门及下级、本部门、本人
    data_scope varchar(64) not null default '本人',
    -- 角色状态：ACTIVE-启用，DISABLED-禁用
    status varchar(32) not null,
    -- 创建时间
    created_at datetime(3) not null,
    -- 更新时间
    updated_at datetime(3) not null,
    unique key uk_sso_role_code (code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table sso_organization (
    -- 组织主键 ID
    id bigint not null primary key,
    -- 父级组织 ID，根组织为空
    parent_id bigint null,
    -- 组织名称
    name varchar(128) not null,
    -- 组织编码，唯一
    code varchar(64) not null,
    -- 负责人
    manager varchar(128) null,
    -- 组织人数，冗余展示字段
    user_count int not null default 0,
    -- 排序号，越小越靠前
    sort_order int not null default 0,
    -- 组织状态：ACTIVE-启用，DISABLED-停用
    status varchar(32) not null,
    -- 创建时间
    created_at datetime(3) not null,
    -- 更新时间
    updated_at datetime(3) not null,
    unique key uk_sso_organization_code (code),
    key idx_sso_organization_parent_id (parent_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table sso_permission (
    -- 权限主键 ID
    id bigint not null primary key,
    -- 权限编码，唯一
    code varchar(128) not null,
    -- 权限名称
    name varchar(64) not null,
    -- 权限类型：APP-应用，MENU-菜单，PAGE-页面，ACTION-操作，API-接口
    type varchar(32) not null,
    -- 父级权限 ID，支持树形结构
    parent_id bigint null,
    -- 资源标识或访问路径
    resource varchar(255) null,
    -- 权限描述
    description varchar(255) null,
    -- 排序号，越小越靠前
    sort_order int not null default 0,
    -- 权限状态：ACTIVE-启用，DISABLED-禁用
    status varchar(32) not null,
    -- 创建时间
    created_at datetime(3) not null,
    -- 更新时间
    updated_at datetime(3) not null,
    unique key uk_sso_permission_code (code),
    key idx_sso_permission_parent_id (parent_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table sso_user_role (
    -- 用户 ID
    user_id bigint not null,
    -- 角色 ID
    role_id bigint not null,
    -- 绑定时间
    created_at datetime(3) not null,
    primary key (user_id, role_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table sso_role_permission (
    -- 角色 ID
    role_id bigint not null,
    -- 权限 ID
    permission_id bigint not null,
    -- 绑定时间
    created_at datetime(3) not null,
    primary key (role_id, permission_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table sso_client (
    -- 客户端主键 ID
    id bigint not null primary key,
    -- OAuth2 Client ID，唯一
    client_id varchar(128) not null,
    -- 客户端密钥哈希，可空，SPA 等公共客户端不需要
    client_secret_hash varchar(255) null,
    -- 客户端名称
    client_name varchar(128) not null,
    -- 客户端类型：SPA-公共单页应用，WEB-服务端 Web，SERVICE-服务间调用，MOBILE-移动端
    client_type varchar(32) not null,
    -- 回调地址列表，JSON 数组
    redirect_uris json not null,
    -- 退出回调地址列表，JSON 数组
    post_logout_redirect_uris json null,
    -- 授权模式列表，JSON 数组
    grant_types json not null,
    -- Scope 列表，JSON 数组
    scopes json not null,
    -- Access Token 有效期，单位秒
    access_token_ttl int not null,
    -- Refresh Token 有效期，单位秒
    refresh_token_ttl int not null,
    -- 是否需要授权确认：true-需要，false-不需要
    require_consent boolean not null default true,
    -- 客户端状态：ENABLED-启用，DISABLED-禁用
    status varchar(32) not null,
    -- 创建时间
    created_at datetime(3) not null,
    -- 更新时间
    updated_at datetime(3) not null,
    unique key uk_sso_client_client_id (client_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table sso_app (
    -- 应用主键 ID
    id bigint not null primary key,
    -- 关联的客户端 ID，可空
    client_id varchar(128) null,
    -- 应用编码，唯一
    code varchar(64) not null,
    -- 应用名称
    name varchar(128) not null,
    -- 应用描述
    description varchar(512) null,
    -- 应用入口地址
    entry_url varchar(512) not null,
    -- 应用图标地址
    logo_url varchar(512) null,
    -- 应用分类
    category varchar(64) not null,
    -- 技术标签列表，JSON 数组
    tech_tags json not null,
    -- 可见性：PUBLIC-公开，LOGIN-登录后可见，AUTHORIZED-授权后可见，ADMIN-管理员可见
    visibility varchar(32) not null,
    -- 排序号，越小越靠前
    sort_order int not null default 0,
    -- 应用状态：ENABLED-启用，DISABLED-禁用，DRAFT-草稿
    status varchar(32) not null,
    -- 创建时间
    created_at datetime(3) not null,
    -- 更新时间
    updated_at datetime(3) not null,
    unique key uk_sso_app_code (code),
    key idx_sso_app_client_id (client_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table sso_user_app (
    -- 用户 ID
    user_id bigint not null,
    -- 应用编码，对应 sso_app.code
    app_code varchar(64) not null,
    -- 授权时间
    created_at datetime(3) not null,
    primary key (user_id, app_code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table sso_audit_log (
    -- 审计日志主键 ID
    id bigint not null primary key,
    -- 操作人 ID
    actor_id bigint null,
    -- 操作人名称
    actor_name varchar(128) null,
    -- 事件类型，例如 LOGIN_SUCCESS、CLIENT_CREATED
    event_type varchar(64) not null,
    -- 目标类型，例如 USER、CLIENT、ROLE
    target_type varchar(64) null,
    -- 目标标识
    target_id varchar(128) null,
    -- 结果：SUCCESS-成功，FAILED-失败
    result varchar(32) not null,
    -- 失败原因
    failure_reason varchar(512) null,
    -- 来源 IP
    ip_address varchar(64) null,
    -- User-Agent
    user_agent varchar(512) null,
    -- 请求 ID
    request_id varchar(64) null,
    -- 业务扩展数据，JSON
    metadata json null,
    -- 创建时间
    created_at datetime(3) not null,
    key idx_sso_audit_created_at (created_at),
    key idx_sso_audit_event_type (event_type)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;
