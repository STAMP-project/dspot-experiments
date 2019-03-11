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
package integration.system;


import com.jayway.restassured.RestAssured;
import integration.BaseRestTest;
import integration.BaseRestTestHelper;
import integration.RequiresAuthentication;
import integration.RequiresVersion;
import org.junit.Test;


@RequiresVersion(">=1.1.0")
@RequiresAuthentication
public class StatsTest extends BaseRestTest {
    private static final String resourcePrefix = "/system/stats";

    @Test
    public void testSystemStats() throws Exception {
        RestAssured.given().when().get(StatsTest.resourcePrefix).then().statusCode(200).assertThat().body(".", BaseRestTestHelper.containsAllKeys("fs", "jvm", "network", "os", "process"));
    }

    @Test
    public void testFsStats() throws Exception {
        RestAssured.given().when().get(((StatsTest.resourcePrefix) + "/fs")).then().statusCode(200).assertThat().body(".", BaseRestTestHelper.containsAllKeys("filesystems"));
    }

    @Test
    public void testJvmStats() throws Exception {
        RestAssured.given().when().get(((StatsTest.resourcePrefix) + "/jvm")).then().statusCode(200);
    }

    @Test
    public void testNetworkStats() throws Exception {
        RestAssured.given().when().get(((StatsTest.resourcePrefix) + "/network")).then().statusCode(200);
    }

    @Test
    public void testOsStats() throws Exception {
        RestAssured.given().when().get(((StatsTest.resourcePrefix) + "/os")).then().statusCode(200);
    }

    @Test
    public void testProcessStats() throws Exception {
        RestAssured.given().when().get(((StatsTest.resourcePrefix) + "/process")).then().statusCode(200);
    }
}

