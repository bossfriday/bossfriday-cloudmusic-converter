package cn.bossfriday.cloudmusic.converter.commons;

/**
 * ServiceRuntimeException
 *
 * @author chenx
 */
public class ServiceRuntimeException extends RuntimeException {

    public ServiceRuntimeException(RuntimeException e) {
        super(e);
    }

    public ServiceRuntimeException(String msg) {
        super(msg);
    }

    public ServiceRuntimeException(String msg, RuntimeException e) {
        super(msg, e);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
