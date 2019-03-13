package dev.morphia.mapping.primitives;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class ByteArrayMappingTest extends TestBase {
    @Test
    public void testCharMapping() throws Exception {
        getMorphia().map(ByteArrayMappingTest.ContainsByteArray.class);
        final ByteArrayMappingTest.ContainsByteArray entity = new ByteArrayMappingTest.ContainsByteArray();
        final Byte[] test = new Byte[]{ 6, 9, 1, -122 };
        entity.ba = test;
        getDs().save(entity);
        final ByteArrayMappingTest.ContainsByteArray loaded = getDs().get(entity);
        for (int i = 0; i < (test.length); i++) {
            final Byte c = test[i];
            Assert.assertEquals(c, entity.ba[i]);
        }
        Assert.assertNotNull(loaded.id);
    }

    private static class ContainsByteArray {
        @Id
        private ObjectId id;

        private Byte[] ba;
    }
}

