package com.navercorp.pinpoint.common.server.bo.serializer.trace.v2;


import com.navercorp.pinpoint.common.server.bo.RandomTSpan;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.common.server.bo.SpanChunkBo;
import com.navercorp.pinpoint.common.server.bo.SpanEventBo;
import com.navercorp.pinpoint.common.server.bo.SpanFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class SpanEncoderTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int REPEAT_COUNT = 10;

    private final RandomTSpan randomTSpan = new RandomTSpan();

    private final SpanFactory spanFactory = new SpanFactory();

    private SpanEncoder spanEncoder = new SpanEncoderV0();

    private SpanDecoder spanDecoder = new SpanDecoderV0();

    @Test
    public void testEncodeSpanColumnValue_simpleSpan() throws Exception {
        SpanBo spanBo = randomSpan();
        assertSpan(spanBo);
    }

    @Test
    public void testEncodeSpanColumnValue_simpleSpan_N() throws Exception {
        for (int i = 0; i < (SpanEncoderTest.REPEAT_COUNT); i++) {
            testEncodeSpanColumnValue_simpleSpan();
        }
    }

    @Test
    public void testEncodeSpanColumnValue_complexSpan() throws Exception {
        SpanBo spanBo = randomComplexSpan();
        assertSpan(spanBo);
    }

    @Test
    public void testEncodeSpanColumnValue_complexSpan_N() throws Exception {
        for (int i = 0; i < (SpanEncoderTest.REPEAT_COUNT); i++) {
            testEncodeSpanColumnValue_complexSpan();
        }
    }

    @Test
    public void testEncodeSpanColumnValue_simpleSpanChunk() throws Exception {
        SpanChunkBo spanChunkBo = randomSpanChunk();
        assertSpanChunk(spanChunkBo);
    }

    @Test
    public void testEncodeSpanColumnValue_simpleSpanChunk_N() throws Exception {
        for (int i = 0; i < (SpanEncoderTest.REPEAT_COUNT); i++) {
            testEncodeSpanColumnValue_simpleSpanChunk();
        }
    }

    @Test
    public void testEncodeSpanColumnValue_complexSpanChunk() throws Exception {
        SpanChunkBo spanChunkBo = randomComplexSpanChunk();
        assertSpanChunk(spanChunkBo);
    }

    @Test
    public void testEncodeSpanColumnValue_complexSpanChunk_N() throws Exception {
        for (int i = 0; i < (SpanEncoderTest.REPEAT_COUNT); i++) {
            testEncodeSpanColumnValue_complexSpanChunk();
        }
    }

    @Test
    public void testEncodeSpanColumnValue_spanEvent_startTimeDelta_equals() {
        SpanBo spanBo = randomComplexSpan();
        SpanEventBo spanEventBo0 = spanBo.getSpanEventBoList().get(0);
        SpanEventBo spanEventBo1 = spanBo.getSpanEventBoList().get(1);
        spanEventBo1.setStartElapsed(spanEventBo0.getStartElapsed());
        assertSpan(spanBo);
    }

    @Test
    public void testEncodeSpanColumnValue_spanEvent_depth_equals() {
        SpanBo spanBo = randomComplexSpan();
        SpanEventBo spanEventBo0 = spanBo.getSpanEventBoList().get(0);
        SpanEventBo spanEventBo1 = spanBo.getSpanEventBoList().get(1);
        spanEventBo1.setDepth(spanEventBo0.getDepth());
        assertSpan(spanBo);
    }

    @Test
    public void testEncodeSpanColumnValue_spanEvent_service_equals() {
        SpanBo spanBo = randomComplexSpan();
        SpanEventBo spanEventBo0 = spanBo.getSpanEventBoList().get(0);
        SpanEventBo spanEventBo1 = spanBo.getSpanEventBoList().get(1);
        spanEventBo1.setServiceType(spanEventBo0.getServiceType());
        assertSpan(spanBo);
    }
}

