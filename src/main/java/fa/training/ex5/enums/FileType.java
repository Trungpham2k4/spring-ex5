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

    public static FileType fromMimeType(String mimeType) {
        for (FileType type : FileType.values()) {
            if (type.getMimeType().equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
    }
}