insert into sso_organization (
    id, parent_id, name, code, manager, user_count, sort_order, status, created_at, updated_at
) values
    (7001, null, 'Mirage 总部', 'HQ', 'Mirage Admin', 16, 1, 'ACTIVE', now(3), now(3)),
    (7002, 7001, '平台研发部', 'PLATFORM', 'Mirage Admin', 8, 10, 'ACTIVE', now(3), now(3)),
    (7003, 7002, '身份认证组', 'IAM', 'admin', 4, 11, 'ACTIVE', now(3), now(3)),
    (7004, 7002, '权限治理组', 'RBAC', 'operator', 4, 12, 'ACTIVE', now(3), now(3)),
    (7005, 7001, '运营体验组', 'OPS', 'operator', 5, 20, 'ACTIVE', now(3), now(3)),
    (7006, 7001, '外部协作', 'EXT', 'guest-owner', 3, 30, 'DISABLED', now(3), now(3));

update sso_role set data_scope = '全部组织' where code = 'ADMIN';
update sso_role set data_scope = '本人' where code = 'USER';
update sso_user set organization_id = 7002 where username = 'admin';
