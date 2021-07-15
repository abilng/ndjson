package in.abilng.ndjson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NdJsonObjectMapper {

    private ObjectMapper objectMapper;

    /**
     * Default constructor, which will construct the default {@link JsonFactory}
     * as necessary, use {@link SerializerProvider} as its
     * {@link SerializerProvider}, and {@link BeanSerializerFactory} as its
     * {@link SerializerFactory}. This means that it can serialize all standard
     * JDK types, as well as regular Java Beans (based on method names and
     * Jackson-specific annotations), but does not support JAXB annotations.
     */
    public NdJsonObjectMapper() {
        this(null, null, null);
    }

    /**
     * Constructs instance that uses specified {@link JsonFactory} for
     * constructing necessary {@link JsonParser}s and/or {@link JsonGenerator}s.
     *
     * @param jf JsonFactory to use: if null, a new {@link MappingJsonFactory}
     *           will be constructed
     */
    public NdJsonObjectMapper(JsonFactory jf) {
        this(jf, null, null);
    }

    /**
     * Constructs instance that uses specified {@link JsonFactory} for
     * constructing necessary {@link JsonParser}s and/or {@link JsonGenerator}s,
     * and uses given providers for accessing serializers and deserializers.
     *
     * @param jf JsonFactory to use: if null, a new {@link MappingJsonFactory}
     *           will be constructed
     * @param sp SerializerProvider to use: if null, a
     *           {@link SerializerProvider} will be constructed
     * @param dc Blueprint deserialization context instance to use for creating
     *           actual context objects; if null, will construct standard
     *           {@link DeserializationContext}
     */
    public NdJsonObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc) {
        objectMapper = new ObjectMapper(jf, sp, dc)
                .configure(SerializationFeature.CLOSE_CLOSEABLE, false)
                .disable(SerializationFeature.CLOSE_CLOSEABLE)
                .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }

    /**
     * Method for registering a module that can extend functionality provided by
     * this mapper; for example, by adding providers for custom serializers and
     * deserializers.
     *
     * @param module Module to register
     * @return NdJsonObjectMapper
     */
    public NdJsonObjectMapper registerModule(Module module) {
        objectMapper.registerModule(module);
        return this;
    }

    /**
     * Convenience method for registering specified modules in order;
     * functionally equivalent to:
     *
     * <pre>
     * for (Module module : modules) {
     * 	registerModule(module);
     * }
     * </pre>
     *
     * @param modules Modules to register
     * @return NdJsonObjectMapper
     */
    public NdJsonObjectMapper registerModules(Module... modules) {
        for (Module module : modules) {
            registerModule(module);
        }
        return this;
    }

    /**
     * Method to deserialize JSON Stream into Stream of Java type, reference to
     * which is passed as argument.
     *
     * @param in        Input Stream
     * @param valueType Class of Object
     * @param <T>       The expected class of the Object.
     * @return Stream of Objects
     * @throws IOException if a low-level I/O problem (unexpected end-of-input, network
     *                     error) occurs (passed through as-is without additional
     *                     wrapping
     */
    public <T> Stream<T> readValue(InputStream in, Class<T> valueType)
            throws IOException {
        Objects.requireNonNull(in, "InputStream cannot be null");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        try {
            return reader
                    .lines()
                    .filter(StringUtils::isNotEmpty)
                    .map(json -> jsonToObject(json, valueType));
        } catch (NdJsonRunTimeIOException e) {
            throw e.getCause();
        }

    }

    /**
     * Method to deserialize JSON Stream into List of Java type, reference to
     * which is passed as argument.
     *
     * @param in        Input Stream
     * @param valueType Class of Object
     * @param <T>       The expected class of the Object.
     * @return List of Objects
     * @throws IOException          if a low-level I/O problem (unexpected end-of-input, network
     *                              error) occurs (passed through as-is without additional
     *                              wrapping
     * @throws JsonParseException   if underlying input contains invalid content of type
     *                              {@link JsonParser} supports (JSON for default case)
     * @throws JsonMappingException if the input JSON structure does not match structure expected
     *                              for result type (or has other mismatch issues)
     */
    public <T> List<T> readValueAsList(InputStream in, Class<T> valueType)
            throws IOException, JsonParseException, JsonMappingException {
        return readValue(in, valueType).collect(Collectors.toList());

    }

    /**
     * Method that can be used to serialize any Java Stream of value as JSON
     * output, written to OutputStream provided.
     *
     * @param out   Output Stream
     * @param value Stream of Java Objects
     * @throws IOException if a low-level I/O problem (unexpected end-of-input, network
     *                     error) occurs (passed through as-is without additional
     *                     wrapping
     */
    public void writeValue(OutputStream out, Stream<Object> value)
            throws IOException {
        Objects.requireNonNull(out, "OutPutStream cannot be null");
        try {
            value.forEach(val -> objectToJson(out, val));
        } catch (NdJsonRunTimeIOException e) {
            throw e.getCause();
        }
    }

    /**
     * Method that can be used to serialize any Java List of value as JSON
     * output, written to OutputStream provided.
     *
     * @param out   Output Stream
     * @param value List of Java Objects
     * @throws IOException if a low-level I/O problem (unexpected end-of-input, network
     *                     error) occurs (passed through as-is without additional
     *                     wrapping
     */
    public void writeValue(OutputStream out, List<?> value)
            throws IOException {
        Objects.requireNonNull(out, "OutPutStream cannot be null");
        try {
            value.forEach(val -> objectToJson(out, val));
        } catch (NdJsonRunTimeIOException e) {
            throw e.getCause();
        }
    }

    public void writeValue(File file, List<?> values)
            throws IOException {
        Objects.requireNonNull(file, "File cannot be null");
        try (OutputStream out = new FileOutputStream(file)) {
            writeValue(out, values);
        } catch (NdJsonRunTimeIOException e) {
            throw e.getCause();
        }
    }

    private void objectToJson(OutputStream out, Object val) {
        try {
            objectMapper.writeValue(out, val);
            out.write("\n".getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            throw new NdJsonRunTimeIOException(e);
        }
    }

    private <T> T jsonToObject(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new NdJsonRunTimeIOException(e);
        }
    }

}
