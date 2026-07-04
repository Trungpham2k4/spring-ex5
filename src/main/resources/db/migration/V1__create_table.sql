CREATE TABLE role (
    role_id UUID PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE CHECK ( role_name IN ('ADMIN', 'TRAINER', 'STUDENT') )
);

CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    status CHAR(8) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE user_role (
    user_id UUID,
    role_id UUID,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
);

CREATE TABLE course (
    course_id UUID PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL,
    duration INTEGER NOT NULL CHECK (duration > 0),
    description TEXT
)