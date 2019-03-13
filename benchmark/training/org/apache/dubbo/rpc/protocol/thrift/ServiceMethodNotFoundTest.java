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
package org.apache.dubbo.rpc.protocol.thrift;


import org.apache.dubbo.common.URL;
import org.junit.jupiter.api.Test;


public class ServiceMethodNotFoundTest extends AbstractTest {
    private URL url;

    @Test
    public void testServiceMethodNotFound() throws Exception {
        // FIXME
        /* url = url.addParameter( "echoString." + Constants.TIMEOUT_KEY, Integer.MAX_VALUE );

        invoker = protocol.refer( Demo.class, url );

        org.junit.jupiter.api.Assertions.assertNotNull( invoker );

        RpcInvocation invocation = new RpcInvocation();

        invocation.setMethodName( "echoString" );

        invocation.setParameterTypes( new Class<?>[]{ String.class } );

        String arg = "Hello, World!";

        invocation.setArguments( new Object[] { arg } );

        invocation.setAttachment(Constants.INTERFACE_KEY, DemoImpl.class.getName());

        Result result = invoker.invoke( invocation );

        Assertions.assertNull( result.getResult() );

        Assertions.assertTrue( result.getException() instanceof RpcException );
         */
    }
}

