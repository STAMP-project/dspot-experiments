package com.baeldung.protobuf;


import com.baeldung.protobuf.BaeldungTraining.Course;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;


@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApplicationIntegrationTest {
    private static final String COURSE1_URL = "http://localhost:8080/courses/1";

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void whenUsingRestTemplate_thenSucceed() {
        ResponseEntity<Course> course = restTemplate.getForEntity(ApplicationIntegrationTest.COURSE1_URL, Course.class);
        assertResponse(course.toString());
    }

    @Test
    public void whenUsingHttpClient_thenSucceed() throws IOException {
        InputStream responseStream = executeHttpRequest(ApplicationIntegrationTest.COURSE1_URL);
        String jsonOutput = convertProtobufMessageStreamToJsonString(responseStream);
        assertResponse(jsonOutput);
    }
}

