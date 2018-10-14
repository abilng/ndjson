package in.abilng.ndjson;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import in.abilng.ndjson.test.pojo.Car;

public class NdJsonObjectMapperTest {

	private NdJsonObjectMapper ndJsonObjectMapper;

	@Before
	public void init() {
		ndJsonObjectMapper = new NdJsonObjectMapper();
	}

	@Test
	public void testReadValueFromImputStream() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("test-car.json").getFile());
		InputStream is = new FileInputStream(file);
		Stream<Car> readValue = ndJsonObjectMapper.readValue(is, Car.class);
		List<Car> list = readValue.collect(Collectors.toList());
		assertThat(list.size(), is(3));
		assertThat(list.get(0).getName(), is("CAR1"));
		assertThat(list.get(1).getName(), is("CAR2"));
		assertThat(list.get(2).getName(), is("CAR3"));
	}

	@Test
	public void testReadValueAsListFromImputStream() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("test-car.json").getFile());
		InputStream is = new FileInputStream(file);
		List<Car> list = ndJsonObjectMapper.readValueAsList(is, Car.class);
		assertThat(list.size(), is(3));
		assertThat(list.get(0).getName(), is("CAR1"));
		assertThat(list.get(1).getName(), is("CAR2"));
		assertThat(list.get(2).getName(), is("CAR3"));
	}

}
