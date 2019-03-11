package io.searchbox.fields;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import com.google.common.collect.ImmutableMap;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;


@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class FieldCapabilitiesIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX = "twitter";

    private static final String TYPE = "tweet";

    private static final String TEST_FIELD = "test_name";

    private static final List FIELDS = Collections.singletonList(FieldCapabilitiesIntegrationTest.TEST_FIELD);

    @Test
    public void testFieldStats() throws IOException {
        Map<String, String> source = ImmutableMap.of(FieldCapabilitiesIntegrationTest.TEST_FIELD, "testFieldStats");
        DocumentResult documentResult = client.execute(new Index.Builder(source).index(FieldCapabilitiesIntegrationTest.INDEX).type(FieldCapabilitiesIntegrationTest.TYPE).refresh(true).build());
        assertTrue(documentResult.getErrorMessage(), documentResult.isSucceeded());
        FieldCapabilities fieldCapabilities = new FieldCapabilities.Builder(FieldCapabilitiesIntegrationTest.FIELDS).build();
        JestResult fieldCapabilitiesResult = client.execute(fieldCapabilities);
        assertTrue(fieldCapabilitiesResult.getErrorMessage(), fieldCapabilitiesResult.isSucceeded());
    }
}

