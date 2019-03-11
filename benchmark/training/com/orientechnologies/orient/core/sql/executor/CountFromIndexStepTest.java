package com.orientechnologies.orient.core.sql.executor;


import OIndexIdentifier.Type;
import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.sql.parser.OIndexIdentifier;
import com.orientechnologies.orient.core.sql.parser.OIndexName;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by olena.kolesnyk on 28/07/2017.
 */
@RunWith(Parameterized.class)
public class CountFromIndexStepTest extends TestUtilsFixture {
    private static final String PROPERTY_NAME = "testPropertyName";

    private static final String PROPERTY_VALUE = "testPropertyValue";

    private static final String ALIAS = "size";

    private static String indexName;

    private Type identifierType;

    public CountFromIndexStepTest(OIndexIdentifier.Type identifierType) {
        this.identifierType = identifierType;
    }

    @Test
    public void shouldCountRecordsOfIndex() {
        OIndexName name = new OIndexName((-1));
        name.setValue(CountFromIndexStepTest.indexName);
        OIndexIdentifier identifier = new OIndexIdentifier((-1));
        identifier.setIndexName(name);
        identifier.setIndexNameString(name.getValue());
        identifier.setType(identifierType);
        OBasicCommandContext context = new OBasicCommandContext();
        context.setDatabase(TestUtilsFixture.database);
        CountFromIndexStep step = new CountFromIndexStep(identifier, CountFromIndexStepTest.ALIAS, context, false);
        OResultSet result = step.syncPull(context, 20);
        Assert.assertEquals(20, ((long) (result.next().getProperty(CountFromIndexStepTest.ALIAS))));
        Assert.assertFalse(result.hasNext());
    }
}

