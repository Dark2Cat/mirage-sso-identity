-- sso_permission 新增管理权限：
-- type：APP-应用，MENU-菜单，PAGE-页面，ACTION-操作，API-接口；status：ACTIVE-启用，DISABLED-禁用。
merge into sso_permission (
    id, code, name, type, parent_id, resource, description, sort_order, status, created_at, updated_at
) key(id) values
    (3005, 'sso:user:manage', '用户管理', 'ACTION', 3001, '用户创建/编辑/禁用/重置密码', '维护用户资料、状态和密码', 15, 'ACTIVE', now(), now()),
    (3006, 'sso:role:manage', '角色管理', 'MENU', null, '/admin/roles', '维护角色和角色授权', 35, 'ACTIVE', now(), now()),
    (3007, 'sso:permission:manage', '权限管理', 'MENU', null, '/admin/permissions', '维护权限资源定义', 36, 'ACTIVE', now(), now()),
    (3008, 'sso:org:manage', '组织管理', 'MENU', null, '/admin/organizations', '维护组织部门结构', 37, 'ACTIVE', now(), now());

merge into sso_role_permission (role_id, permission_id, created_at) key(role_id, permission_id) values
    (2001, 3005, now()),
    (2001, 3006, now()),
    (2001, 3007, now()),
    (2001, 3008, now());
