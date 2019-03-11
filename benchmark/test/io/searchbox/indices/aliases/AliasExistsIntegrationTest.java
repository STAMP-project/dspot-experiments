package io.searchbox.indices.aliases;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.junit.Test;


@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class AliasExistsIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX_NAME_1 = "aliases_test_index1";

    private static final String INDEX_NAME_2 = "aliases_test_index2";

    @Test
    public void testAliasesExists() throws IOException {
        String alias = "myAlias000";
        createAlias(AliasExistsIntegrationTest.INDEX_NAME_1, alias);
        AliasExists aliasExists = new AliasExists.Builder().build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasDoesNotExist() throws IOException {
        AliasExists aliasExists = new AliasExists.Builder().alias("does_not_exist").build();
        JestResult result = client.execute(aliasExists);
        assertFalse(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testIndexDoesNotExist() throws IOException {
        AliasExists aliasExists = new AliasExists.Builder().addIndex("does_not_exist").build();
        JestResult result = client.execute(aliasExists);
        assertFalse(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasAndIndexDoesNotExist() throws IOException {
        AliasExists aliasExists = new AliasExists.Builder().addIndex("does_not_exist").alias("abc").build();
        JestResult result = client.execute(aliasExists);
        assertFalse(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasesExistsForSpecificIndex() throws IOException {
        String alias = "myAlias000";
        createAlias(AliasExistsIntegrationTest.INDEX_NAME_1, alias);
        AliasExists aliasExists = new AliasExists.Builder().addIndex(AliasExistsIntegrationTest.INDEX_NAME_1).build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasesExistsForMultipleIndices() throws IOException {
        String alias = "myAlias000";
        createAlias(AliasExistsIntegrationTest.INDEX_NAME_1, alias);
        AliasExists aliasExists = new AliasExists.Builder().addIndex(AliasExistsIntegrationTest.INDEX_NAME_1).addIndex(AliasExistsIntegrationTest.INDEX_NAME_2).build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasesExistsForSpecificAlias() throws IOException {
        String alias = "myAlias000";
        createAlias(AliasExistsIntegrationTest.INDEX_NAME_1, alias);
        AliasExists aliasExists = new AliasExists.Builder().alias(alias).build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasesExistsForSpecificAliasAndIndex() throws IOException {
        String alias = "myAlias000";
        createAlias(AliasExistsIntegrationTest.INDEX_NAME_1, alias);
        AliasExists aliasExists = new AliasExists.Builder().addIndex(AliasExistsIntegrationTest.INDEX_NAME_1).alias(alias).build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
}

