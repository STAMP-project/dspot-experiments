/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package integration.system.grok;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import integration.BaseRestTest;
import integration.BaseRestTestHelper;
import integration.RequiresAuthentication;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


@RequiresAuthentication
public class GrokTests extends BaseRestTest {
    private String id;

    @Test
    public void createGrokPattern() {
        final ValidatableResponse validatableResponse = RestAssured.given().when().body(jsonResourceForMethod()).post("/system/grok").then().statusCode(201).statusLine(CoreMatchers.notNullValue());
        id = validatableResponse.extract().body().jsonPath().get("id").toString();
        validatableResponse.body(".", BaseRestTestHelper.containsAllKeys("id", "name", "pattern", "content_pack"));
    }

    @Test
    public void listPatterns() {
        createGrokPattern();
        // we have just created one pattern, so we should find it again.
        RestAssured.given().when().get("/system/grok/{patternid}", id).then().statusCode(200).statusLine(CoreMatchers.notNullValue()).body(".", BaseRestTestHelper.containsAllKeys("id", "name", "pattern", "content_pack"));
    }

    @Test
    public void deletePattern() {
        createGrokPattern();
        RestAssured.given().when().delete("/system/grok/{patternid}", id).then().statusCode(204);
    }
}

