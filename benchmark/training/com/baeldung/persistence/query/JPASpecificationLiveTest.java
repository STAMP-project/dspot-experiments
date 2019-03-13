package com.baeldung.persistence.query;


import com.baeldung.persistence.model.User;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;


// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(classes = { ConfigTest.class,
// PersistenceConfig.class }, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class JPASpecificationLiveTest {
    // @Autowired
    // private UserRepository repository;
    private User userJohn;

    private User userTom;

    private final String URL_PREFIX = "http://localhost:8082/spring-rest-query-language/auth/users/spec?search=";

    private final String EURL_PREFIX = "http://localhost:8082/spring-rest-query-language/auth/users/espec?search=";

    @Test
    public void givenFirstOrLastName_whenGettingListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((EURL_PREFIX) + "firstName:john,'lastName:doe"));
        final String result = response.body().asString();
        Assert.assertTrue(result.contains(userJohn.getEmail()));
        Assert.assertTrue(result.contains(userTom.getEmail()));
    }

    @Test
    public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((URL_PREFIX) + "firstName:john,lastName:doe"));
        final String result = response.body().asString();
        Assert.assertTrue(result.contains(userJohn.getEmail()));
        Assert.assertFalse(result.contains(userTom.getEmail()));
    }

    @Test
    public void givenFirstNameInverse_whenGettingListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((URL_PREFIX) + "firstName!john"));
        final String result = response.body().asString();
        Assert.assertTrue(result.contains(userTom.getEmail()));
        Assert.assertFalse(result.contains(userJohn.getEmail()));
    }

    @Test
    public void givenMinAge_whenGettingListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((URL_PREFIX) + "age>25"));
        final String result = response.body().asString();
        Assert.assertTrue(result.contains(userTom.getEmail()));
        Assert.assertFalse(result.contains(userJohn.getEmail()));
    }

    @Test
    public void givenFirstNamePrefix_whenGettingListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((URL_PREFIX) + "firstName:jo*"));
        final String result = response.body().asString();
        Assert.assertTrue(result.contains(userJohn.getEmail()));
        Assert.assertFalse(result.contains(userTom.getEmail()));
    }

    @Test
    public void givenFirstNameSuffix_whenGettingListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((URL_PREFIX) + "firstName:*n"));
        final String result = response.body().asString();
        Assert.assertTrue(result.contains(userJohn.getEmail()));
        Assert.assertFalse(result.contains(userTom.getEmail()));
    }

    @Test
    public void givenFirstNameSubstring_whenGettingListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((URL_PREFIX) + "firstName:*oh*"));
        final String result = response.body().asString();
        Assert.assertTrue(result.contains(userJohn.getEmail()));
        Assert.assertFalse(result.contains(userTom.getEmail()));
    }

    @Test
    public void givenAgeRange_whenGettingListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((URL_PREFIX) + "age>20,age<25"));
        final String result = response.body().asString();
        Assert.assertTrue(result.contains(userJohn.getEmail()));
        Assert.assertFalse(result.contains(userTom.getEmail()));
    }

    private final String ADV_URL_PREFIX = "http://localhost:8082/spring-rest-query-language/auth/users/spec/adv?search=";

    @Test
    public void givenFirstOrLastName_whenGettingAdvListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((ADV_URL_PREFIX) + "firstName:john OR lastName:doe"));
        final String result = response.body().asString();
        Assert.assertTrue(result.contains(userJohn.getEmail()));
        Assert.assertTrue(result.contains(userTom.getEmail()));
    }

    @Test
    public void givenFirstOrFirstNameAndAge_whenGettingAdvListOfUsers_thenCorrect() {
        final Response response = RestAssured.get(((ADV_URL_PREFIX) + "( firstName:john OR firstName:tom ) AND age>22"));
        final String result = response.body().asString();
        Assert.assertFalse(result.contains(userJohn.getEmail()));
        Assert.assertTrue(result.contains(userTom.getEmail()));
    }
}

