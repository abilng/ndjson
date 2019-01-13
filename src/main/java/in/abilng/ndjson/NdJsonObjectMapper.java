package in.abilng.ndjson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

import in.abilng.ndjson.internal.NdJsonRunTimeException;

public class NdJsonObjectMapper {

	private ObjectMapper objectMapper = null;

	/**
	 * Default constructor, which will construct the default {@link JsonFactory}
	 * as necessary, use {@link SerializerProvider} as its
	 * {@link SerializerProvider}, and {@link BeanSerializerFactory} as its
	 * {@link SerializerFactory}. This means that it can serialize all standard
	 * JDK types, as well as regular Java Beans (based on method names and
	 * Jackson-specific annotations), but does not support JAXB annotations.
	 */
	public NdJsonObjectMapper() {
		objectMapper = new ObjectMapper();
	}

	/**
	 * Constructs instance that uses specified {@link JsonFactory} for
	 * constructing necessary {@link JsonParser}s and/or {@link JsonGenerator}s.
	 */
	public NdJsonObjectMapper(JsonFactory jf) {
		objectMapper = new ObjectMapper(jf);
	}

	/**
	 * Constructs instance that uses specified {@link JsonFactory} for
	 * constructing necessary {@link JsonParser}s and/or {@link JsonGenerator}s,
	 * and uses given providers for accessing serializers and deserializers.
	 * 
	 * @param jf
	 *            JsonFactory to use: if null, a new {@link MappingJsonFactory}
	 *            will be constructed
	 * @param sp
	 *            SerializerProvider to use: if null, a
	 *            {@link SerializerProvider} will be constructed
	 * @param dc
	 *            Blueprint deserialization context instance to use for creating
	 *            actual context objects; if null, will construct standard
	 *            {@link DeserializationContext}
	 */
	public NdJsonObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc) {
		objectMapper = new ObjectMapper(jf, sp, dc);
	}

	/**
	 * Method for registering a module that can extend functionality provided by
	 * this mapper; for example, by adding providers for custom serializers and
	 * deserializers.
	 * 
	 * @param module
	 *            Module to register
	 * 
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
	 * @throws NdJsonRunTimeException
	 *             if a low-level I/O problem (unexpected end-of-input, network
	 *             error) occurs (passed through as-is without additional
	 *             wrapping
	 * @throws JsonParseException
	 *             if underlying input contains invalid content of type
	 *             {@link JsonParser} supports (JSON for default case)
	 * @throws JsonMappingException
	 *             if the input JSON structure does not match structure expected
	 *             for result type (or has other mismatch issues)
	 */
	public <T> Stream<T> readValue(InputStream inputStream, Class<T> valueType)
			throws NdJsonRunTimeException, JsonParseException, JsonMappingException {
		Objects.requireNonNull(inputStream, "InputStream cannot be null");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		return reader
				.lines()
				.filter(StringUtils::isNotEmpty)
				.map(json -> jsonToObject(json, valueType));
	}

	/**
	 * Method to deserialize JSON Stream into List of Java type, reference to
	 * which is passed as argument.
	 * 
	 * @throws NdJsonRunTimeException
	 *             if a low-level I/O problem (unexpected end-of-input, network
	 *             error) occurs (passed through as-is without additional
	 *             wrapping
	 * @throws JsonParseException
	 *             if underlying input contains invalid content of type
	 *             {@link JsonParser} supports (JSON for default case)
	 * @throws JsonMappingException
	 *             if the input JSON structure does not match structure expected
	 *             for result type (or has other mismatch issues)
	 */
	public <T> List<T> readValueAsList(InputStream src, Class<T> valueType)
			throws NdJsonRunTimeException, JsonParseException, JsonMappingException {
		return readValue(src, valueType).collect(Collectors.toList());
	}

	/**
	 * Method that can be used to serialize any Java Stream of value as JSON
	 * output, written to OutputStream provided.
	 * 
	 * @param out
	 *            Output Stream
	 * @param value
	 *            Stream of Java Objects
	 * @throws NdJsonRunTimeException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 */
	public void writeValue(OutputStream out, Stream<Object> value)
			throws NdJsonRunTimeException, JsonGenerationException, JsonMappingException {
		value.forEach(val -> objectToJson(out, val));
	}

	/**
	 * Method that can be used to serialize any Java List of value as JSON
	 * output, written to OutputStream provided.
	 * 
	 * @param out
	 *            Output Stream
	 * @param value
	 *            List of Java Objects
	 * @throws NdJsonRunTimeException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 */
	public void writeValue(OutputStream out, List<Object> value)
			throws NdJsonRunTimeException, JsonGenerationException, JsonMappingException {
		value.forEach(val -> objectToJson(out, val));
	}

	private void objectToJson(OutputStream out, Object val) {
		try {
			objectMapper.writeValue(out, val);
		} catch (IOException e) {
			throw new NdJsonRunTimeException(e);
		}
	}

	private <T> T jsonToObject(String json, Class<T> valueType) {
		try {
			return objectMapper.readValue(json, valueType);
		} catch (IOException e) {
			throw new NdJsonRunTimeException(e);
		}
	}

}
