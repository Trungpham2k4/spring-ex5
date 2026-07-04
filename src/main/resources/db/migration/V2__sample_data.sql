INSERT INTO users (user_id, username, password, status)
VALUES (gen_random_uuid(), 'admin', '$2a$10$ncuD.daPqKftDpHhDnoOk.T8SPew6FmBFBjKOTtUoYnhdiK9iJNpy', 'ACTIVE');

INSERT INTO role (role_id, role_name)
VALUES (gen_random_uuid(), 'ADMIN');
VALUES (gen_random_uuid(), 'TRAINER');
VALUES (gen_random_uuid(), 'STUDENT');

INSERT INTO user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM "users" u JOIN role r ON r.role_name = 'ADMIN'
WHERE u.username = 'admin';


