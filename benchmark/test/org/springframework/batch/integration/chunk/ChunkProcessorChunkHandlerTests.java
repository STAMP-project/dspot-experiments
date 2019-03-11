package org.springframework.batch.integration.chunk;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.util.StringUtils;


public class ChunkProcessorChunkHandlerTests {
    private ChunkProcessorChunkHandler<Object> handler = new ChunkProcessorChunkHandler();

    protected int count = 0;

    @Test
    public void testVanillaHandleChunk() throws Exception {
        handler.setChunkProcessor(new org.springframework.batch.core.step.item.ChunkProcessor<Object>() {
            public void process(StepContribution contribution, Chunk<Object> chunk) throws Exception {
                count += chunk.size();
            }
        });
        StepContribution stepContribution = MetaDataInstanceFactory.createStepExecution().createStepContribution();
        ChunkResponse response = handler.handleChunk(new ChunkRequest(0, StringUtils.commaDelimitedListToSet("foo,bar"), 12L, stepContribution));
        Assert.assertEquals(stepContribution, response.getStepContribution());
        Assert.assertEquals(12, response.getJobId().longValue());
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(2, count);
    }
}

