package com.mongodb.hadoop.pig;


import org.apache.pig.ResourceSchema;
import org.apache.pig.impl.util.Utils;
import org.junit.Assert;
import org.junit.Test;


public class BSONStorageTest {
    @Test
    public void testNullMap() throws Exception {
        ResourceSchema schema = new ResourceSchema(Utils.getSchemaFromString("m:map[]"));
        Assert.assertNull(BSONStorage.getTypeForBSON(null, schema.getFields()[0], null));
    }
}

