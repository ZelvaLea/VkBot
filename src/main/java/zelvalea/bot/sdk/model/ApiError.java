package zelvalea.bot.sdk.model;

public class ApiError {
    private int error_code;
    private String error_msg;

    public int getErrorCode() {return error_code;}

    public String getErrorMessage() {return error_msg;}

    @Override
    public String toString() {
        return "ApiError{" +
                "error_code=" + error_code +
                ", error_msg='" + error_msg + '\'' +
                '}';
    }
}
