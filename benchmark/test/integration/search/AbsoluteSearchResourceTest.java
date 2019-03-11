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
package integration.search;


import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.ValidatableResponse;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import integration.RequiresAuthentication;
import integration.RestTestIncludingElasticsearch;
import org.junit.Ignore;
import org.junit.Test;


@RequiresAuthentication
@Ignore("Flakey test")
public class AbsoluteSearchResourceTest extends RestTestIncludingElasticsearch {
    @Test
    @UsingDataSet(locations = "searchForExistingKeyword.json")
    public void searchForAllMessages() {
        final ValidatableResponse result = doSearchFor("*");
        final JsonPath response = result.statusCode(200).extract().jsonPath();
        assertThat(response.getInt("total_results")).isEqualTo(2);
        assertThat(response.getList("messages")).hasSize(2);
        assertThat(response.getList("used_indices")).hasSize(1);
    }

    @Test
    @UsingDataSet(locations = "searchForExistingKeyword.json")
    public void searchForExistingKeyword() {
        final ValidatableResponse result = doSearchFor("Testmessage");
        final JsonPath response = result.statusCode(200).extract().jsonPath();
        assertThat(response.getInt("total_results")).isEqualTo(1);
        assertThat(response.getList("messages")).hasSize(1);
        assertThat(response.getList("used_indices")).hasSize(1);
    }

    @Test
    @UsingDataSet(locations = "searchForExistingKeyword.json")
    public void searchForNonexistingKeyword() {
        final ValidatableResponse result = doSearchFor("Nonexistent");
        final JsonPath response = result.statusCode(200).extract().jsonPath();
        assertThat(response.getInt("total_results")).isEqualTo(0);
        assertThat(response.getList("messages")).isEmpty();
        assertThat(response.getList("used_indices")).hasSize(1);
    }

    @Test
    @UsingDataSet(locations = "searchForExistingKeyword.json")
    public void searchForExistingKeywordOutsideOfTimeRange() {
        final ValidatableResponse result = doSearchFor("Testmessage", "2015-06-14T11:32:16.827Z", "2015-06-15T11:32:16.827Z");
        final JsonPath response = result.statusCode(200).extract().jsonPath();
        assertThat(response.getInt("total_results")).isEqualTo(0);
        assertThat(response.getList("messages")).isEmpty();
        assertThat(response.getList("used_indices")).hasSize(1);
    }

    @Test
    @UsingDataSet(locations = "searchForExistingKeyword.json")
    public void searchForExistingKeywordInsideOfVeryNarrowTimeRange() {
        final ValidatableResponse result = doSearchFor("Testmessage", "2015-06-16T11:32:16.827Z", "2015-06-16T11:32:16.827Z");
        final JsonPath response = result.statusCode(200).extract().jsonPath();
        assertThat(response.getInt("total_results")).isEqualTo(1);
        assertThat(response.getList("messages")).isNotEmpty();
        assertThat(response.getList("used_indices")).hasSize(1);
    }
}

