package in.abilng.ndjson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.abilng.ndjson.internal.NdJsonRunTimeException;

public class NdJsonObjectMapper {

	private ObjectMapper objectMapper = null;

	public NdJsonObjectMapper() {
		objectMapper = new ObjectMapper();
	}

	public <T> Stream<T> readValue(InputStream inputStream, Class<T> valueType)
			throws NdJsonRunTimeException {
		Objects.requireNonNull(inputStream, "InputStream cannot be null");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		return reader
				.lines()
				.filter(StringUtils::isNotEmpty)
				.map(json -> jsonToObject(json, valueType));
	}

	public <T> List<T> readValueAsList(InputStream src, Class<T> valueType) throws NdJsonRunTimeException {
		return readValue(src, valueType).collect(Collectors.toList());
	}

	private <T> T jsonToObject(String json, Class<T> valueType) {
		try {
			return objectMapper.readValue(json, valueType);
		} catch (IOException e) {
			throw new NdJsonRunTimeException(e);
		}
	}

}
