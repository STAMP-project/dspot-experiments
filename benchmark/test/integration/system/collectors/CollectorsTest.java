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
package integration.system.collectors;


import com.jayway.restassured.RestAssured;
import integration.BaseRestTest;
import integration.RequiresAuthentication;
import integration.RequiresVersion;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;


@RequiresVersion(">=1.1.0")
@Ignore("Collector code moved into a plugin. We have to figure out how to run these tests.")
public class CollectorsTest extends BaseRestTest {
    private final String resourcePrefix = "/system/collectors";

    @Test
    public void testRegisterCollector() throws Exception {
        RestAssured.given().when().header("X-Graylog-Collector-Version", "0.0.0").body(jsonResourceForMethod()).put(getResourceEndpoint("collectorId")).then().statusCode(202);
    }

    @Test
    public void testRegisterInvalidCollector() throws Exception {
        RestAssured.given().when().header("X-Graylog-Collector-Version", "0.0.0").body(jsonResourceForMethod()).put(getResourceEndpoint("invalidCollector")).then().statusCode(400);
    }

    @Test
    @RequiresAuthentication
    public void testListCollectors() throws Exception {
        RestAssured.given().when().get(resourcePrefix).then().statusCode(200).assertThat().body("collectors", Matchers.notNullValue());
    }

    @Test
    @RequiresAuthentication
    public void testTouchCollector() throws Exception {
        final String collectorId = "testTouchCollectorId";
        final String collectorId2 = "testTouchCollectorId2";
        RestAssured.given().when().auth().none().header("X-Graylog-Collector-Version", "0.0.0").body(jsonResourceForMethod()).put(getResourceEndpoint(collectorId)).then().statusCode(202);
        final DateTime lastSeenBefore = getLastSeenForCollectorId(collectorId);
        RestAssured.given().when().auth().none().header("X-Graylog-Collector-Version", "0.0.0").body(jsonResource("test-register-collector.json")).put(getResourceEndpoint(collectorId2)).then().statusCode(202);
        final DateTime lastSeenAfterOtherRegistration = getLastSeenForCollectorId(collectorId);
        RestAssured.given().when().auth().none().header("X-Graylog-Collector-Version", "0.0.0").body(jsonResourceForMethod()).put(getResourceEndpoint(collectorId)).then().statusCode(202);
        final DateTime lastSeenAfter = getLastSeenForCollectorId(collectorId);
        assertThat(lastSeenBefore).isEqualTo(lastSeenAfterOtherRegistration);
        assertThat(lastSeenBefore).isNotEqualTo(lastSeenAfter).isBefore(lastSeenAfter);
    }
}

