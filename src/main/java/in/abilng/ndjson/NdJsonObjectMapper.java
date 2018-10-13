package in.abilng.ndjson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NdJsonObjectMapper {

	private ObjectMapper objectMapper = null;

	public NdJsonObjectMapper() {
		objectMapper = new ObjectMapper();
	}

	public <T> Stream<T> readValue(InputStream src, Class<T> valueType)
			throws JsonParseException, JsonMappingException, IOException {
		objectMapper.readValue("", valueType);
		return null;
	}

	public <T> List<T> readValueAsList(InputStream src, Class<T> valueType)
			throws JsonParseException, JsonMappingException, IOException {
		objectMapper.readValue("", valueType);
		return null;
	}

}
