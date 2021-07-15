package in.abilng.ndjson;

import java.io.IOException;

/**
 * This Class is used to catch IO Exception Carry them during Java Stream
 * Processing
 *
 * @author abgeorge
 */
final class NdJsonRunTimeIOException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final IOException cause;

    public NdJsonRunTimeIOException(IOException e) {
        super(e);
        cause = e;
    }

    @Override
    public IOException getCause() {
        return cause;
    }

}
