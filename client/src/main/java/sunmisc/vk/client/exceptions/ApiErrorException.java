package sunmisc.vk.client.exceptions;

public class ApiErrorException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1333741234023622233L;

    private final String description;
    private final int code;

    public ApiErrorException(String description, int code) {
        super("HTTP error " + code + ": " + description);
        this.description = description;
        this.code = code;
    }
    public String description() {
        return description;
    }

    public int errorCode() {
        return code;
    }

}
