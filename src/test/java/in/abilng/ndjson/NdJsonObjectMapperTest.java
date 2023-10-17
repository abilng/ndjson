package in.abilng.ndjson;

import in.abilng.ndjson.test.pojo.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NdJsonObjectMapperTest {

    private NdJsonObjectMapper ndJsonObjectMapper;

    @BeforeEach
    public void init() {
        ndJsonObjectMapper = new NdJsonObjectMapper();
    }

    @Test
    public void testReadValueFromInputStream() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("test-car.json").toURI());
        InputStream is = Files.newInputStream(path);
        Stream<Car> readValue = ndJsonObjectMapper.readValue(is, Car.class);
        List<Car> list = readValue.collect(Collectors.toList());
        assertThat(list.size(), is(3));
        assertThat(list.get(0).getName(), is("CAR1"));
        assertThat(list.get(1).getName(), is("CAR2"));
        assertThat(list.get(2).getName(), is("CAR3"));
    }

    @Test
    public void testReadValueAsListFromInputStream() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("test-car.json").toURI());
        InputStream is = Files.newInputStream(path);
        List<Car> list = ndJsonObjectMapper.readValueAsList(is, Car.class);
        assertThat(list.size(), is(3));
        assertThat(list.get(0).getName(), is("CAR1"));
        assertThat(list.get(1).getName(), is("CAR2"));
        assertThat(list.get(2).getName(), is("CAR3"));
    }

    @Test
    public void testWriteValuesToInputStream() throws Exception {
        OutputStream out = null;
        InputStream in = null;
        try {
            Path path = Files.createTempFile("test-write-list", ".json");
            List<Object> values = Arrays.asList(new Car("C1"), new Car("C2"));
            out = Files.newOutputStream(path);
            ndJsonObjectMapper.writeValue(out, values);

            in = Files.newInputStream(path);
            List<Car> list = ndJsonObjectMapper.readValueAsList(in, Car.class);
            assertThat(list.size(), is(2));
            assertThat(list.get(0).getName(), is("C1"));
            assertThat(list.get(1).getName(), is("C2"));
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

}
