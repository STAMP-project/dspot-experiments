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
package integration.roles;


import com.jayway.restassured.RestAssured;
import integration.BaseRestTest;
import integration.BaseRestTestHelper;
import integration.RequiresAuthentication;
import org.junit.Test;


@RequiresAuthentication
public class RolesResourceTest extends BaseRestTest {
    @Test
    public void deleteRolesTest() {
        try {
            RestAssured.given().when().body(jsonResource("create-role.json")).post("/roles").then().statusCode(201);
            RestAssured.given().when().body(jsonResource("create-user-with-role.json")).post("/users").then().statusCode(201);
            RestAssured.given().when().get("/users/{name}", "john").then().statusCode(200).body("roles", BaseRestTestHelper.setContainsEntry("Test"));
            RestAssured.given().when().delete("/roles/{name}", "Test").then().statusCode(204);
            RestAssured.given().when().get("/users/{name}", "john").then().statusCode(200).body("roles", BaseRestTestHelper.setDoesNotContain("Test"));
        } finally {
            // still remove the user even though it's not technically necessary
            RestAssured.given().when().delete("/users/{name}", "john").then().statusCode(204);
        }
    }
}

