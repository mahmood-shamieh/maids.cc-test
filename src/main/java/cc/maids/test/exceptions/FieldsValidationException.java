package cc.maids.test.exceptions;

import java.util.HashMap;
import java.util.Map;

public class FieldsValidationException extends Exception {
    private Map errors;

    public FieldsValidationException(String message, Map errors) {
        super(message);
        this.errors = errors;
    }

    public Map getErrors() {
        return errors;
    }
}
