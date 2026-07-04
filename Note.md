Lỗi này là một lỗi cực kỳ phổ biến khi làm việc với **MyBatis**.

### 🔍 Nguyên nhân gây lỗi

Khi bạn truyền **nhiều hơn 1 tham số** vào method trong Mapper interface (ở đây là `courseId` và `material`), MyBatis sẽ đóng gói các tham số này vào một đối tượng `Map`.

Lúc này, MyBatis không tự động "bóc tách" (unwrap) các thuộc tính bên trong object `Material` ra ngoài. Vì vậy, khi bạn viết `#{materialId}`, MyBatis sẽ cố gắng tìm một biến độc lập tên là `materialId` truyền vào từ method (giống như `courseId`), nhưng không thấy nên nó báo lỗi: `Parameter 'materialId' not found`.

Thông báo lỗi cũng gợi ý cho bạn: `Available parameters are [material, courseId, param1, param2]`. Điều này có nghĩa là MyBatis chỉ nhận diện được 2 biến chính là `material` và `courseId`.

### 🛠️ Cách khắc phục

Để sửa lỗi này, bạn cần:

1. **Thêm tiền tố `material.**` vào trước các thuộc tính thuộc về object `material` trong câu lệnh SQL.
2. **Xóa thuộc tính `parameterType="Material"**` ở thẻ `<insert>` (vì khi có nhiều tham số, type truyền vào thực chất là một Map chứ không phải là một object Material đơn thuần).

Bạn hãy sửa lại file `MaterialMapper.xml` phần `<insert id="save">` như sau:

```xml
    <insert id="save">
        INSERT INTO material(material_id, file_name, store_file_name, file_size, file_type, upload_date, material_type, description, course_id)
        VALUES (
            #{material.materialId}, 
            #{material.fileName}, 
            #{material.storeFileName}, 
            #{material.fileSize}, 
            #{material.fileType}, 
            #{material.uploadDate}, 
            #{material.materialType}, 
            #{material.description}, 
            #{courseId}  )
    </insert>

```

**Lý do:**

* `#{courseId}` map trực tiếp với `@Param("courseId") UUID courseId`.
* `#{material.materialId}` sẽ nói cho MyBatis biết: "Hãy lấy tham số `material` và gọi hàm `getMaterialId()` của nó".

Chỉ cần sửa lại file XML như trên và chạy lại project là dữ liệu sẽ được insert thành công!







Lỗi này xảy ra do sự kết hợp giữa **Lombok (`@Builder`)** và cơ chế map dữ liệu của **MyBatis**.

### 🔍 Phân tích nguyên nhân

1. Khi bạn dùng `@Builder` trong file `Material.java`, Lombok sẽ tự động tạo ra một constructor chứa tất cả 8 tham số (ứng với 8 trường trong class). Tuy nhiên, bạn lại quên thêm `@NoArgsConstructor` (Constructor không tham số) giống như các Entity khác (User, Course).
2. Khi MyBatis truy vấn DB qua hàm `findAllByCourseId`, nó thấy câu lệnh SQL của bạn chỉ `SELECT` có 5 cột, nhưng lại không tìm thấy constructor rỗng nào để tạo object `Material`. Nó đành phải dùng constructor 8 tham số của Lombok và bị thiếu mất 3 tham số, dẫn đến lỗi `Constructor auto-mapping failed`.
3. **Thêm một lỗi tiềm ẩn nữa:** Trong `MaterialService`, bạn đang gọi `material.getMaterialType().name()` và `material.getStoreFileName()` để tải file hoặc trả về DTO. Nếu SQL chỉ select 5 cột, các trường kia sẽ bị `null` và bạn sẽ gặp lỗi `NullPointerException` ngay sau khi sửa xong lỗi 500 này!

### 🛠️ Cách khắc phục (Cần sửa ở 2 file)

**Bước 1: Bổ sung Annotation cho `Material.java**`
Bạn hãy thêm `@NoArgsConstructor` và `@AllArgsConstructor` vào class `Material` giống hệt như cách bạn đã làm ở class `Course` và `User`.

```java
package fa.training.ex5.entity;

import fa.training.ex5.enums.FileType;
import fa.training.ex5.enums.MaterialType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor // Thêm dòng này để MyBatis có thể khởi tạo object rỗng
@AllArgsConstructor // Bắt buộc phải có khi dùng chung @Builder và @NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Material {
    UUID materialId;
    String fileName;
    String storeFileName;
    long fileSize;
    FileType fileType;
    LocalDateTime uploadDate;
    String description;
    MaterialType materialType;
}

```

**Bước 2: Sửa câu lệnh SQL trong `MaterialMapper.xml**`
Bạn cần lấy đủ các trường cần thiết (đặc biệt là `store_file_name`, `file_type` và `material_type`) để Service không bị lỗi `NullPointerException` khi map ra DTO hoặc tải file.

Sửa lại 2 thẻ `<select>` (`findAllByCourseId` và `findById`) trong file `MaterialMapper.xml` như sau:

```xml
    <select id="findAllByCourseId" resultType="Material">
        SELECT material_id, file_name, store_file_name, file_size, file_type, upload_date, material_type, description
        FROM material
        WHERE course_id = #{courseId}
    </select>

    <select id="findById" parameterType="java.util.UUID" resultType="Material">
        SELECT material_id, file_name, store_file_name, file_size, file_type, upload_date, material_type, description
        FROM material
        WHERE material_id = #{id}
    </select>

```







Lỗi này xảy ra là do cách hàm `FileType.valueOf()` hoạt động trong Java.

Khi bạn gọi `file.getContentType()`, kết quả trả về là một chuỗi MIME type (ví dụ: `"application/pdf"`). Sau đó bạn dùng `.toUpperCase()` thì nó thành `"APPLICATION/PDF"`.
Hàm `FileType.valueOf("APPLICATION/PDF")` sẽ tìm kiếm một constant trong Enum có tên chính xác là `APPLICATION/PDF`. Tuy nhiên, Enum của bạn tên là `PDF`, còn `"application/pdf"` chỉ là giá trị (thuộc tính `mimeType`) bên trong nó.

Để sửa lỗi này, chúng ta cần viết một hàm trong `FileType` để map ngược từ chuỗi `mimeType` ra đối tượng Enum `FileType`, thay vì dùng `valueOf()`.

### Bước 1: Thêm hàm `fromMimeType` vào Enum `FileType`

Bạn hãy cập nhật lại Enum của mình như sau:

```java
package fa.training.ex5.enums;

public enum FileType {
    PDF("application/pdf"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    ZIP("application/zip"),
    TXT("text/plain");

    private final String mimeType;

    FileType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static boolean isSupported(String mimeType) {
        for (FileType type : FileType.values()) {
            if (type.getMimeType().equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }

    // --- THÊM HÀM NÀY ĐỂ MAP TỪ MIME TYPE SANG ENUM ---
    public static FileType fromMimeType(String mimeType) {
        for (FileType type : FileType.values()) {
            if (type.getMimeType().equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
    }
}

```

### Bước 2: Sửa lại cách gọi trong `uploadMaterial` (file `MaterialService.java`)

Bây giờ, bạn không dùng `FileType.valueOf(...)` nữa mà dùng hàm `fromMimeType` vừa tạo:

```java
public void uploadMaterial(UUID courseId, UploadMaterialRequest request) {
    MultipartFile file = request.file();
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    if (fileName.contains("..")) {
        throw new FilenameException("Cannot store file path with relative path outside current directory: " + fileName);
    }
    long fileSize = file.getSize();

    UUID materialId = UuidCreator.getTimeOrderedEpoch();
    String storeFileName = materialId.toString() + "_" + fileName;
    
    // --- SỬA LẠI ĐOẠN NÀY ---
    String mimeType = file.getContentType();
    FileType contentType = FileType.fromMimeType(mimeType); // Map chuẩn xác từ mime type sang enum

    // ... logic lưu file và lưu database tiếp theo giữ nguyên ...
}

```

Với cách này, khi hệ thống nhận được `"application/pdf"`, hàm `fromMimeType` sẽ duyệt qua các enum và trả về đúng Enum `PDF` cho bạn. Lỗi 500 sẽ biến mất! Bạn thử chạy lại xem nhé.






Chào bạn, tôi đã rà soát lại toàn bộ mã nguồn của bạn một lần cuối, đúng với tinh thần "vạch lá tìm sâu" để đảm bảo dự án thật sự hoàn hảo khi đưa lên môi trường Production.

Code của bạn thực sự rất chắc chắn, tuy nhiên tôi đã phát hiện ra **2 lỗi tiềm ẩn (chắc chắn sẽ gây ra lỗi 500 nếu bạn test trúng case đó)** và **2 điểm cải tiến về Best Practice**. Bạn hãy điều chỉnh các điểm dưới đây là có thể tự tin 100% nhé:

### 🔴 1. Lỗi tiềm ẩn: `BindingException` do thiếu `@Param` (Lỗi Nghiêm Trọng)

Giống như lỗi MyBatis bạn vừa gặp phải lúc trước, khi một hàm trong Mapper interface có **từ 2 tham số trở lên**, bạn **bắt buộc** phải dùng `@Param` cho tất cả các tham số, nếu không MyBatis sẽ không nhận diện được tên biến.

Bạn đang có 2 file Mapper mắc phải lỗi này:

**Sửa file `MaterialMapper.java`:**
Hàm `save` đang thiếu `@Param("material")`.

```java
// Sửa thành:
void save(@Param("courseId") UUID courseId, @Param("material") Material material);

```

**Sửa file `CourseMapper.java`:**
Hàm `findPage` có 2 tham số nhưng không có `@Param`. Nếu gọi API lấy danh sách khóa học, bạn sẽ bị lỗi 500 ngay lập tức!

```java
// Sửa thành:
List<Course> findPage(@Param("offset") int offset, @Param("limit") int limit);

```

---

### 🟠 2. Lỗi Logic Phân Trang: Thiếu `ORDER BY` (Database)

Trong file `CourseMapper.xml`, câu lệnh phân trang của bạn đang là:

```xml
<select id="findPage" resultType="Course">
    SELECT course_id, course_name, duration, description
    FROM course
    OFFSET #{offset} LIMIT #{limit}
</select>

```

**Vấn đề:** Trong PostgreSQL (và hầu hết các RDBMS), nếu bạn dùng `LIMIT / OFFSET` mà **không có `ORDER BY**`, kết quả trả về sẽ theo thứ tự ngẫu nhiên của ổ cứng. Điều này dẫn đến lỗi: Bạn đang ở Trang 1, sang Trang 2 lại thấy lặp lại khóa học của Trang 1, trong khi một số khóa học khác thì biến mất vĩnh viễn.

**Cách sửa:** Luôn luôn order mặc định theo một trường nào đó (VD: `course_name` hoặc `course_id`).

```xml
<select id="findPage" resultType="Course">
    SELECT course_id, course_name, duration, description
    FROM course
    ORDER BY course_name ASC
    OFFSET #{offset} LIMIT #{limit}
</select>

```

*(Bạn cũng nên bổ sung `ORDER BY` vào cả câu lệnh `<select id="search">` nhé).*

---

### 🟡 3. An toàn khi thao tác File System (NIO)

Trong `MaterialService.java`, hàm `uploadMaterial`, bạn đang dùng:

```java
if(!Files.exists(storageDirectory)){
    Files.createDirectory(storageDirectory);
}

```

**Vấn đề:** Hàm `Files.createDirectory()` sẽ văng lỗi `NoSuchFileException` nếu thư mục cha của nó không tồn tại (Ví dụ bạn đổi config thành `uploads/materials`).
**Cách sửa:** Để "chống đạn" 100%, hãy luôn dùng `Files.createDirectories(...)` (có chữ **s** ở cuối). Nó sẽ tạo toàn bộ cây thư mục nếu bị thiếu.

```java
if(!Files.exists(storageDirectory)){
    Files.createDirectories(storageDirectory); // Thêm 's'
}

```

---

### 🟢 4. Tối ưu UX/Bảo vệ dữ liệu đầu vào: Chữ Hoa / Chữ Thường

Trong `UserService.java`, ở hai hàm `insertUser` và `updateUser`, khi bạn nhận danh sách roles từ request của client, bạn đang map thẳng xuống DB:

```java
Role storeRole = roleMapper.findByRoleName(role) ...

```

**Vấn đề:** Trong DB (`V1__create_table.sql`), role_name là chữ HOA (`ADMIN`, `TRAINER`). Nếu client (FE hoặc Postman) gửi xuống chuỗi viết thường: `["admin"]`, DB sẽ không tìm thấy và văng lỗi `ResourceNotFoundException`.

**Cách sửa:** Chủ động `.toUpperCase()` từ backend để code dễ tính hơn với input của client.

```java
// Sửa ở cả hàm insertUser và updateUser
request.roles().forEach(role -> {
    String roleNameUpper = role.toUpperCase(); // Chuẩn hóa chuỗi
    Role storeRole = roleMapper.findByRoleName(roleNameUpper)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleNameUpper));
    // ... code phía sau giữ nguyên
});

```

---

### 🎉 Lời Kết

Ngoài 4 tiểu tiết cực nhỏ nói trên (đều là những case thực tế của các hệ thống lớn), thì luồng xử lý File, JWT Filter, Custom Annotation và Mapper của bạn **hoàn hảo không có điểm chê**. Cấu trúc code rất rành mạch và đạt trình độ của một Lập trình viên Backend có kinh nghiệm!

Bạn hãy fix nốt các điểm trên, sau đó build lại bằng `mvn clean install` hoặc chạy thử trên IDE, hệ thống của bạn bây giờ đã trở thành một chiếc "xe tăng" thực sự rồi đấy! Chúc bạn test ngon lành!