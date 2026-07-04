CREATE TABLE material (
    material_id UUID PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    store_file_name VARCHAR(255) UNIQUE NOT NULL,
    file_size BIGINT NOT NULL CHECK (file_size > 0),
    file_type VARCHAR(255) NOT NULL,
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    material_type VARCHAR(20) NOT NULL CHECK (material_type IN ('LECTURE', 'ASSIGNMENT', 'REFERENCE', 'SOURCE_CODE')),
    description TEXT,
    course_id UUID NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
)