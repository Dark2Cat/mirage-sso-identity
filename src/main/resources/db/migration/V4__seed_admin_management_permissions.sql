-- 补充管理端权限；type：APP-应用，MENU-菜单，PAGE-页面，ACTION-操作，API-接口；status：ACTIVE-启用，DISABLED-禁用
insert into sso_permission (
    id, code, name, type, parent_id, resource, description, sort_order, status, created_at, updated_at
) values
    (3005, 'sso:user:manage', '用户管理', 'ACTION', 3001, '用户创建/编辑/禁用/重置密码', '维护用户资料、状态和密码', 15, 'ACTIVE', now(3), now(3)),
    (3006, 'sso:role:manage', '角色管理', 'MENU', null, '/admin/roles', '维护角色和角色授权', 35, 'ACTIVE', now(3), now(3)),
    (3007, 'sso:permission:manage', '权限管理', 'MENU', null, '/admin/permissions', '维护权限资源定义', 36, 'ACTIVE', now(3), now(3)),
    (3008, 'sso:org:manage', '组织管理', 'MENU', null, '/admin/organizations', '维护组织部门结构', 37, 'ACTIVE', now(3), now(3))
on duplicate key update
    name = values(name),
    type = values(type),
    parent_id = values(parent_id),
    resource = values(resource),
    description = values(description),
    sort_order = values(sort_order),
    status = values(status),
    updated_at = values(updated_at);

insert ignore into sso_role_permission (role_id, permission_id, created_at) values
    (2001, 3005, now(3)),
    (2001, 3006, now(3)),
    (2001, 3007, now(3)),
    (2001, 3008, now(3));
