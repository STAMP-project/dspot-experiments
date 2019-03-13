/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.codec;


import RpcErrorType.CLIENT_DESERIALIZE;
import RpcErrorType.CLIENT_SERIALIZE;
import RpcErrorType.SERVER_DESERIALIZE;
import RpcErrorType.SERVER_SERIALIZE;
import RpcErrorType.UNKNOWN;
import com.alipay.sofa.rpc.context.RpcInternalContext;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class AbstractSerializerTest extends AbstractSerializer {
    private TestSerializer serializer = new TestSerializer();

    @Test
    public void buildSerializeError() {
        RpcInternalContext old = RpcInternalContext.peekContext();
        try {
            RpcInternalContext.removeContext();
            SofaRpcException exception = serializer.buildSerializeError("xx");
            Assert.assertEquals(UNKNOWN, exception.getErrorType());
            RpcInternalContext.getContext().setProviderSide(true);
            exception = serializer.buildSerializeError("xx");
            Assert.assertEquals(SERVER_SERIALIZE, exception.getErrorType());
            RpcInternalContext.getContext().setProviderSide(false);
            exception = serializer.buildSerializeError("xx");
            Assert.assertEquals(CLIENT_SERIALIZE, exception.getErrorType());
            RpcInternalContext.removeContext();
            exception = serializer.buildSerializeError("xx", new RuntimeException());
            Assert.assertEquals(UNKNOWN, exception.getErrorType());
            RpcInternalContext.getContext().setProviderSide(true);
            exception = serializer.buildSerializeError("xx", new RuntimeException());
            Assert.assertEquals(SERVER_SERIALIZE, exception.getErrorType());
            RpcInternalContext.getContext().setProviderSide(false);
            exception = serializer.buildSerializeError("xx", new RuntimeException());
            Assert.assertEquals(CLIENT_SERIALIZE, exception.getErrorType());
        } finally {
            RpcInternalContext.setContext(old);
        }
    }

    @Test
    public void buildDeserializeError() {
        RpcInternalContext old = RpcInternalContext.peekContext();
        try {
            RpcInternalContext.removeContext();
            SofaRpcException exception = serializer.buildDeserializeError("xx");
            Assert.assertEquals(UNKNOWN, exception.getErrorType());
            RpcInternalContext.getContext().setProviderSide(true);
            exception = serializer.buildDeserializeError("xx");
            Assert.assertEquals(SERVER_DESERIALIZE, exception.getErrorType());
            RpcInternalContext.getContext().setProviderSide(false);
            exception = serializer.buildDeserializeError("xx");
            Assert.assertEquals(CLIENT_DESERIALIZE, exception.getErrorType());
            RpcInternalContext.removeContext();
            exception = serializer.buildDeserializeError("xx", new RuntimeException());
            Assert.assertEquals(UNKNOWN, exception.getErrorType());
            RpcInternalContext.getContext().setProviderSide(true);
            exception = serializer.buildDeserializeError("xx", new RuntimeException());
            Assert.assertEquals(SERVER_DESERIALIZE, exception.getErrorType());
            RpcInternalContext.getContext().setProviderSide(false);
            exception = serializer.buildDeserializeError("xx", new RuntimeException());
            Assert.assertEquals(CLIENT_DESERIALIZE, exception.getErrorType());
        } finally {
            RpcInternalContext.setContext(old);
        }
    }
}

