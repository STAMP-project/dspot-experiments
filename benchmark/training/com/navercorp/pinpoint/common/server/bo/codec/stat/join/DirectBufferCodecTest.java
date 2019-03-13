/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.server.bo.codec.stat.join;


import com.navercorp.pinpoint.common.buffer.AutomaticBuffer;
import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatUtils;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.ApplicationStatDecodingContext;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinStatBo;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Roy Kim
 */
public class DirectBufferCodecTest {
    @Test
    public void encodeAndDecodeTest() {
        final String id = "test_app";
        final long currentTime = new Date().getTime();
        final AgentStatDataPointCodec agentStatDataPointCodec = new AgentStatDataPointCodec();
        final DirectBufferCodec directBufferCodec = new DirectBufferCodec(agentStatDataPointCodec);
        final Buffer encodedValueBuffer = new AutomaticBuffer();
        final List<JoinStatBo> joinDirectBufferBoList = createJoinDirectBufferBoList(currentTime);
        encodedValueBuffer.putByte(directBufferCodec.getVersion());
        directBufferCodec.encodeValues(encodedValueBuffer, joinDirectBufferBoList);
        final Buffer valueBuffer = new com.navercorp.pinpoint.common.buffer.FixedBuffer(encodedValueBuffer.getBuffer());
        final long baseTimestamp = AgentStatUtils.getBaseTimestamp(currentTime);
        final long timestampDelta = currentTime - baseTimestamp;
        final ApplicationStatDecodingContext decodingContext = new ApplicationStatDecodingContext();
        decodingContext.setApplicationId(id);
        decodingContext.setBaseTimestamp(baseTimestamp);
        decodingContext.setTimestampDelta(timestampDelta);
        Assert.assertEquals(valueBuffer.readByte(), directBufferCodec.getVersion());
        List<JoinStatBo> decodedJoinDirectBufferBoList = directBufferCodec.decodeValues(valueBuffer, decodingContext);
        for (int i = 0; i < (decodedJoinDirectBufferBoList.size()); i++) {
            Assert.assertTrue(decodedJoinDirectBufferBoList.get(i).equals(joinDirectBufferBoList.get(i)));
        }
    }
}

