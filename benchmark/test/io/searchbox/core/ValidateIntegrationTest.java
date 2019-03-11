package io.searchbox.core;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import Parameters.EXPLAIN;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class ValidateIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void validateQueryWithIndex() throws IOException {
        Validate validate = new Validate.Builder(("{\n" + (((((((((((("  \"query\" : {\n" + "    \"bool\" : {\n") + "      \"must\" : {\n") + "        \"query_string\" : {\n") + "          \"query\" : \"*:*\"\n") + "        }\n") + "      },\n") + "      \"filter\" : {\n") + "        \"term\" : { \"user\" : \"kimchy\" }\n") + "      }\n") + "    }\n") + "  }\n") + "}"))).index("twitter").setParameter(EXPLAIN, true).build();
        executeTestCase(validate);
    }

    @Test
    public void validateQueryWithIndexAndType() throws IOException {
        executeTestCase(new Validate.Builder(("{\n" + (((((((((((("  \"query\" : {\n" + "    \"bool\" : {\n") + "      \"must\" : {\n") + "        \"query_string\" : {\n") + "          \"query\" : \"*:*\"\n") + "        }\n") + "      },\n") + "      \"filter\" : {\n") + "        \"term\" : { \"user\" : \"kimchy\" }\n") + "      }\n") + "    }\n") + "  }\n") + "}"))).index("twitter").type("tweet").build());
    }
}

