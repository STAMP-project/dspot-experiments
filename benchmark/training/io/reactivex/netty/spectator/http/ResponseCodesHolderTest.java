/**
 * Copyright 2015 Netflix, Inc.
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
package io.reactivex.netty.spectator.http;


import com.netflix.spectator.api.DefaultRegistry;
import com.netflix.spectator.api.Registry;
import io.reactivex.netty.spectator.http.internal.ResponseCodesHolder;
import org.junit.Test;


public class ResponseCodesHolderTest {
    @Test(timeout = 60000)
    public void testGetResponse1xx() throws Exception {
        String monitorId = ResponseCodesHolderTest.newMonitorId();
        Registry registry = new DefaultRegistry();
        ResponseCodesHolder holder = new ResponseCodesHolder(registry, monitorId);
        holder.update(100);
        ResponseCodesHolderTest.checkCodes(holder, 1, 0, 0, 0, 0);
        holder.update(102);
        ResponseCodesHolderTest.checkCodes(holder, 2, 0, 0, 0, 0);
    }

    @Test(timeout = 60000)
    public void testGetResponse2xx() throws Exception {
        String monitorId = ResponseCodesHolderTest.newMonitorId();
        Registry registry = new DefaultRegistry();
        ResponseCodesHolder holder = new ResponseCodesHolder(registry, monitorId);
        holder.update(200);
        ResponseCodesHolderTest.checkCodes(holder, 0, 1, 0, 0, 0);
        holder.update(233);
        ResponseCodesHolderTest.checkCodes(holder, 0, 2, 0, 0, 0);
    }

    @Test(timeout = 60000)
    public void testGetResponse3xx() throws Exception {
        String monitorId = ResponseCodesHolderTest.newMonitorId();
        Registry registry = new DefaultRegistry();
        ResponseCodesHolder holder = new ResponseCodesHolder(registry, monitorId);
        holder.update(300);
        ResponseCodesHolderTest.checkCodes(holder, 0, 0, 1, 0, 0);
        holder.update(365);
        ResponseCodesHolderTest.checkCodes(holder, 0, 0, 2, 0, 0);
    }

    @Test(timeout = 60000)
    public void testGetResponse4xx() throws Exception {
        String monitorId = ResponseCodesHolderTest.newMonitorId();
        Registry registry = new DefaultRegistry();
        ResponseCodesHolder holder = new ResponseCodesHolder(registry, monitorId);
        holder.update(400);
        ResponseCodesHolderTest.checkCodes(holder, 0, 0, 0, 1, 0);
        holder.update(452);
        ResponseCodesHolderTest.checkCodes(holder, 0, 0, 0, 2, 0);
    }

    @Test(timeout = 60000)
    public void testGetResponse5xx() throws Exception {
        String monitorId = ResponseCodesHolderTest.newMonitorId();
        Registry registry = new DefaultRegistry();
        ResponseCodesHolder holder = new ResponseCodesHolder(registry, monitorId);
        holder.update(500);
        ResponseCodesHolderTest.checkCodes(holder, 0, 0, 0, 0, 1);
        holder.update(599);
        ResponseCodesHolderTest.checkCodes(holder, 0, 0, 0, 0, 2);
    }
}

