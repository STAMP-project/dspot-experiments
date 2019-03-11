package cn.zhangxd.config;


import HttpStatus.OK;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApplicationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;

    @Test
    public void configurationAvailable() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = testRestTemplate.withBasicAuth(username, password).getForEntity("/app/cloud", Map.class);
        Assert.assertEquals(OK, entity.getStatusCode());
    }

    @Test
    public void envPostAvailable() {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap<String, String>();
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = testRestTemplate.withBasicAuth(username, password).postForEntity("/admin/env", form, Map.class);
        Assert.assertEquals(OK, entity.getStatusCode());
    }
}

