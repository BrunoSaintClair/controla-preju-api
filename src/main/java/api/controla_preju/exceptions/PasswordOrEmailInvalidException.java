package api.controla_preju.exceptions;

public class PasswordOrEmailInvalidException extends RuntimeException {
    public PasswordOrEmailInvalidException(String message) {
        super(message);
    }
}
