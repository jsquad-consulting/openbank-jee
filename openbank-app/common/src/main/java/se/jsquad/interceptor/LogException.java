package se.jsquad.interceptor;

class LogException extends Exception {
    LogException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
