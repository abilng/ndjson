package in.abilng.ndjson;

import in.abilng.ndjson.test.pojo.Car;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Test
    public void testWriteValuesToInputStream() throws Exception {
        OutputStream out = null;
        FileInputStream in = null;
        try {
            File file = File.createTempFile("test-write-list", ".json");
            List<Object> values = Arrays.asList(new Car("C1"), new Car("C2"));
            out = new FileOutputStream(file);
            ndJsonObjectMapper.writeValue(out, values);

            in = new FileInputStream(file);
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
