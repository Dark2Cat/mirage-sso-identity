-- sso_permission 新增应用访问权限：
-- type：APP-应用；status：ACTIVE-启用，DISABLED-禁用。
merge into sso_permission (
    id, code, name, type, parent_id, resource, description, sort_order, status, created_at, updated_at
) key(id) values
    (3101, 'app:oa:access', 'Mirage OA 访问', 'APP', 3004, 'oa', '访问 Mirage OA', 101, 'ACTIVE', now(), now()),
    (3102, 'app:crm:access', 'Mirage CRM 访问', 'APP', 3004, 'crm', '访问 Mirage CRM', 102, 'ACTIVE', now(), now()),
    (3103, 'app:mall:access', 'Mirage Mall 访问', 'APP', 3004, 'mall', '访问 Mirage Mall', 103, 'ACTIVE', now(), now()),
    (3104, 'app:ai-lab:access', 'AI Workbench 访问', 'APP', 3004, 'ai-lab', '访问 AI Workbench', 104, 'ACTIVE', now(), now());

merge into sso_role_permission (role_id, permission_id, created_at) key(role_id, permission_id) values
    (2001, 3101, now()),
    (2001, 3102, now()),
    (2001, 3103, now()),
    (2001, 3104, now()),
    (2002, 3101, now()),
    (2002, 3102, now());
