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
package com.alipay.sofa.rpc.context;


import RemotingConstants.RPC_REQUEST_BAGGAGE;
import com.alipay.sofa.rpc.common.RemotingConstants;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class BaggageResolverTest {
    private RpcInvokeContext old = null;

    @Test
    public void carryWithRequest() throws Exception {
        SofaRequest request = new SofaRequest();
        BaggageResolver.carryWithRequest(null, request);
        Assert.assertNull(request.getRequestProps());
        RpcInvokeContext context = new RpcInvokeContext();
        BaggageResolver.carryWithRequest(context, request);
        Assert.assertNull(request.getRequestProps());
        context.putRequestBaggage("xx", "11");
        context.putRequestBaggage("??", "??");
        BaggageResolver.carryWithRequest(context, request);
        Assert.assertTrue(((request.getRequestProps()) != null));
        Map<String, String> map = ((Map<String, String>) (request.getRequestProp(RPC_REQUEST_BAGGAGE)));
        Assert.assertNotNull(map);
        Assert.assertEquals(map.get("xx"), "11");
        Assert.assertEquals(map.get("??"), "??");
    }

    @Test
    public void pickupFromRequest() throws Exception {
        // ?????
        SofaRequest request = new SofaRequest();
        BaggageResolver.pickupFromRequest(null, request);
        Assert.assertNull(RpcInvokeContext.peekContext());
        BaggageResolver.pickupFromRequest(null, request, true);
        Assert.assertNull(RpcInvokeContext.peekContext());
        RpcInvokeContext context = new RpcInvokeContext();
        BaggageResolver.pickupFromRequest(context, request);
        Assert.assertNull(RpcInvokeContext.peekContext());
        Assert.assertNull(context.getRequestBaggage("xx"));
        Assert.assertNull(context.getRequestBaggage("??"));
        // ?????
        Map<String, String> reqBaggage = new HashMap<String, String>();
        reqBaggage.put("xx", "11");
        reqBaggage.put("??", "??");
        request.addRequestProp(RPC_REQUEST_BAGGAGE, reqBaggage);
        BaggageResolver.pickupFromRequest(context, request);
        Assert.assertNull(RpcInvokeContext.peekContext());
        Assert.assertNull(RpcInvokeContext.peekContext());
        Assert.assertEquals(context.getRequestBaggage("xx"), "11");
        Assert.assertEquals(context.getRequestBaggage("??"), "??");
        // ??????
        BaggageResolver.pickupFromRequest(null, request);
        Assert.assertNull(RpcInvokeContext.peekContext());
        BaggageResolver.pickupFromRequest(null, request, true);
        Assert.assertNotNull(RpcInvokeContext.peekContext());
        Assert.assertEquals(RpcInvokeContext.getContext().getRequestBaggage("xx"), "11");
        Assert.assertEquals(RpcInvokeContext.getContext().getRequestBaggage("??"), "??");
    }

    @Test
    public void carryWithResponse() throws Exception {
        SofaResponse response = new SofaResponse();
        BaggageResolver.carryWithResponse(null, response);
        Assert.assertNull(response.getResponseProps());
        RpcInvokeContext context = new RpcInvokeContext();
        BaggageResolver.carryWithResponse(context, response);
        Assert.assertNull(response.getResponseProps());
        context.putResponseBaggage("xx", "11");
        context.putResponseBaggage("??", "??");
        BaggageResolver.carryWithResponse(context, response);
        Assert.assertTrue(((response.getResponseProps()) != null));
        Assert.assertEquals(response.getResponseProp("rpc_resp_baggage.xx"), "11");
        Assert.assertEquals(response.getResponseProp("rpc_resp_baggage.??"), "??");
    }

    @Test
    public void pickupFromResponse() throws Exception {
        // ?????
        SofaResponse response = new SofaResponse();
        BaggageResolver.pickupFromResponse(null, response);
        Assert.assertNull(RpcInvokeContext.peekContext());
        BaggageResolver.pickupFromResponse(null, response, true);
        Assert.assertNull(RpcInvokeContext.peekContext());
        RpcInvokeContext context = new RpcInvokeContext();
        BaggageResolver.pickupFromResponse(context, response);
        Assert.assertNull(RpcInvokeContext.peekContext());
        Assert.assertNull(context.getResponseBaggage("xx"));
        Assert.assertNull(context.getResponseBaggage("??"));
        // ?????
        response.addResponseProp(((RemotingConstants.RPC_RESPONSE_BAGGAGE) + ".xx"), "11");
        response.addResponseProp(((RemotingConstants.RPC_RESPONSE_BAGGAGE) + ".??"), "??");
        BaggageResolver.pickupFromResponse(context, response);
        Assert.assertNull(RpcInvokeContext.peekContext());
        Assert.assertNull(RpcInvokeContext.peekContext());
        Assert.assertEquals(context.getResponseBaggage("xx"), "11");
        Assert.assertEquals(context.getResponseBaggage("??"), "??");
        // ??????
        BaggageResolver.pickupFromResponse(null, response);
        Assert.assertNull(RpcInvokeContext.peekContext());
        BaggageResolver.pickupFromResponse(null, response, true);
        Assert.assertNotNull(RpcInvokeContext.peekContext());
        Assert.assertEquals(RpcInvokeContext.getContext().getResponseBaggage("xx"), "11");
        Assert.assertEquals(RpcInvokeContext.getContext().getResponseBaggage("??"), "??");
    }
}

