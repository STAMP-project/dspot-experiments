package com.baeldung.jackson.format;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import java.util.Date;
import org.junit.Test;


/**
 *
 *
 * @author Jay Sridhar
 * @version 1.0
 */
public class JsonFormatUnitTest {
    @Test
    public void whenSerializedDateFormat_thenCorrect() throws JsonProcessingException {
        User user = new User("Jay", "Sridhar");
        String result = new ObjectMapper().writeValueAsString(user);
        // Expected to match: "2016-12-19@09:34:42.628+0000"
        assertThat(JsonPath.from(result).getString("createdDate")).matches("\\d{4}\\-\\d{2}\\-\\d{2}@\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\+\\d{4}");
        // Expected to be close to current time
        long now = new Date().getTime();
        assertThat(JsonPath.from(result).getLong("dateNum")).isCloseTo(now, withPercentage(10.0));
    }
}

