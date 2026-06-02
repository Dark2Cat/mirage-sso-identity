-- sso_user 字段：
-- id-用户主键 ID；username-用户名，唯一，登录账号；email-邮箱，可空；
-- phone-手机号，可空；organization_id-用户所属组织 ID，可空；password_hash-密码哈希；nickname-昵称；
-- avatar_url-头像地址；status-用户状态：PENDING-待验证，ACTIVE-正常，DISABLED-已禁用，LOCKED-已锁定，DELETED-已删除；
-- last_login_at-最近登录时间；created_at-创建时间；updated_at-更新时间；deleted_at-逻辑删除时间。
create table sso_user (
    id bigint not null primary key,
    username varchar(64) not null,
    email varchar(128) null,
    phone varchar(32) null,
    organization_id bigint null,
    password_hash varchar(255) not null,
    nickname varchar(64) null,
    avatar_url varchar(512) null,
    status varchar(32) not null,
    last_login_at timestamp null,
    created_at timestamp not null,
    updated_at timestamp not null,
    deleted_at timestamp null
);
create unique index uk_sso_user_username on sso_user (username);
create unique index uk_sso_user_email on sso_user (email);
create unique index uk_sso_user_phone on sso_user (phone);

-- sso_role 字段：
-- id-角色主键 ID；code-角色编码，唯一；name-角色名称；description-角色描述；data_scope-数据范围；
-- status-角色状态：ACTIVE-启用，DISABLED-禁用；created_at-创建时间；updated_at-更新时间。
create table sso_role (
    id bigint not null primary key,
    code varchar(64) not null,
    name varchar(64) not null,
    description varchar(255) null,
    data_scope varchar(64) not null default '本人',
    status varchar(32) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);
create unique index uk_sso_role_code on sso_role (code);

-- sso_organization 字段：
-- id-组织主键 ID；parent_id-父级组织 ID；name-组织名称；code-组织编码，唯一；
-- manager-负责人；user_count-组织人数；sort_order-排序号；status-组织状态：ACTIVE-启用，DISABLED-停用；
-- created_at-创建时间；updated_at-更新时间。
create table sso_organization (
    id bigint not null primary key,
    parent_id bigint null,
    name varchar(128) not null,
    code varchar(64) not null,
    manager varchar(128) null,
    user_count int not null default 0,
    sort_order int not null default 0,
    status varchar(32) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);
create unique index uk_sso_organization_code on sso_organization (code);
create index idx_sso_organization_parent_id on sso_organization (parent_id);

-- sso_permission 字段：
-- id-权限主键 ID；code-权限编码，唯一；name-权限名称；
-- type-权限类型：APP-应用，MENU-菜单，PAGE-页面，ACTION-操作，API-接口；
-- parent_id-父级权限 ID；resource-资源标识；description-权限描述；sort_order-排序号；
-- status-权限状态：ACTIVE-启用，DISABLED-禁用；created_at-创建时间；updated_at-更新时间。
create table sso_permission (
    id bigint not null primary key,
    code varchar(128) not null,
    name varchar(64) not null,
    type varchar(32) not null,
    parent_id bigint null,
    resource varchar(255) null,
    description varchar(255) null,
    sort_order int not null default 0,
    status varchar(32) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);
create unique index uk_sso_permission_code on sso_permission (code);

-- sso_user_role 字段：user_id-用户 ID；role_id-角色 ID；created_at-绑定时间。
create table sso_user_role (
    user_id bigint not null,
    role_id bigint not null,
    created_at timestamp not null,
    primary key (user_id, role_id)
);

-- sso_role_permission 字段：role_id-角色 ID；permission_id-权限 ID；created_at-绑定时间。
create table sso_role_permission (
    role_id bigint not null,
    permission_id bigint not null,
    created_at timestamp not null,
    primary key (role_id, permission_id)
);

-- sso_client 字段：
-- id-客户端主键 ID；client_id-OAuth2 Client ID，唯一；client_secret_hash-客户端密钥哈希；
-- client_name-客户端名称；client_type-客户端类型：SPA-公共单页应用，WEB-服务端 Web，SERVICE-服务间调用，MOBILE-移动端；
-- redirect_uris-回调地址列表；post_logout_redirect_uris-退出回调地址列表；grant_types-授权模式列表；scopes-Scope 列表；
-- access_token_ttl-Access Token 有效期秒；refresh_token_ttl-Refresh Token 有效期秒；
-- require_consent-是否需要授权确认：true-需要，false-不需要；status-客户端状态：ENABLED-启用，DISABLED-禁用；
-- created_at-创建时间；updated_at-更新时间。
create table sso_client (
    id bigint not null primary key,
    client_id varchar(128) not null,
    client_secret_hash varchar(255) null,
    client_name varchar(128) not null,
    client_type varchar(32) not null,
    redirect_uris varchar(2000) not null,
    post_logout_redirect_uris varchar(2000) null,
    grant_types varchar(2000) not null,
    scopes varchar(2000) not null,
    access_token_ttl int not null,
    refresh_token_ttl int not null,
    require_consent boolean not null default true,
    status varchar(32) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);
create unique index uk_sso_client_client_id on sso_client (client_id);

-- sso_app 字段：
-- id-应用主键 ID；client_id-关联客户端 ID；code-应用编码，唯一；name-应用名称；description-应用描述；
-- entry_url-应用入口地址；logo_url-应用图标地址；category-应用分类；tech_tags-技术标签列表；
-- visibility-可见性：PUBLIC-公开，LOGIN-登录后可见，AUTHORIZED-授权后可见，ADMIN-管理员可见；
-- sort_order-排序号；status-应用状态：ENABLED-启用，DISABLED-禁用，DRAFT-草稿；
-- created_at-创建时间；updated_at-更新时间。
create table sso_app (
    id bigint not null primary key,
    client_id varchar(128) null,
    code varchar(64) not null,
    name varchar(128) not null,
    description varchar(512) null,
    entry_url varchar(512) not null,
    logo_url varchar(512) null,
    category varchar(64) not null,
    tech_tags varchar(2000) not null,
    visibility varchar(32) not null,
    sort_order int not null default 0,
    status varchar(32) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);
create unique index uk_sso_app_code on sso_app (code);

-- sso_audit_log 字段：
-- id-审计日志主键 ID；actor_id-操作人 ID；actor_name-操作人名称；event_type-事件类型；
-- target_type-目标类型；target_id-目标标识；result-结果：SUCCESS-成功，FAILED-失败；
-- failure_reason-失败原因；ip_address-来源 IP；user_agent-User-Agent；request_id-请求 ID；
-- metadata-业务扩展数据；created_at-创建时间。
create table sso_audit_log (
    id bigint not null primary key,
    actor_id bigint null,
    actor_name varchar(128) null,
    event_type varchar(64) not null,
    target_type varchar(64) null,
    target_id varchar(128) null,
    result varchar(32) not null,
    failure_reason varchar(512) null,
    ip_address varchar(64) null,
    user_agent varchar(512) null,
    request_id varchar(64) null,
    metadata varchar(2000) null,
    created_at timestamp not null
);

-- sso_user_app 字段：
-- user_id-用户 ID；app_code-应用编码，对应 sso_app.code；created_at-授权时间。
create table sso_user_app (
    user_id bigint not null,
    app_code varchar(64) not null,
    created_at timestamp not null,
    primary key (user_id, app_code)
);
