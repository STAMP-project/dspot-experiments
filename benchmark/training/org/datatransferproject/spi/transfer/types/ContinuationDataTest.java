package org.datatransferproject.spi.transfer.types;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.datatransferproject.types.common.IntPaginationToken;
import org.datatransferproject.types.common.models.IdOnlyContainerResource;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ContinuationDataTest {
    @Test
    public void verifySerializeDeserialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerSubtypes(ContinuationData.class, IntPaginationToken.class, IdOnlyContainerResource.class);
        ContinuationData continuationData = new ContinuationData(new IntPaginationToken(100));
        continuationData.addContainerResource(new IdOnlyContainerResource("123"));
        String serialized = objectMapper.writeValueAsString(continuationData);
        ContinuationData deserialized = objectMapper.readValue(serialized, ContinuationData.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(100, getStart());
        Assert.assertEquals("123", getId());
    }
}

