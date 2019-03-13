/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.monostate;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Date: 12/21/15 - 12:26 PM
 *
 * @author Jeroen Meulemeester
 */
public class LoadBalancerTest {
    @Test
    public void testSameStateAmongstAllInstances() {
        final LoadBalancer firstBalancer = new LoadBalancer();
        final LoadBalancer secondBalancer = new LoadBalancer();
        firstBalancer.addServer(new Server("localhost", 8085, 6));
        // Both should have the same number of servers.
        Assertions.assertEquals(firstBalancer.getNoOfServers(), secondBalancer.getNoOfServers());
        // Both Should have the same LastServedId
        Assertions.assertEquals(firstBalancer.getLastServedId(), secondBalancer.getLastServedId());
    }

    @Test
    public void testServe() {
        final Server server = Mockito.mock(Server.class);
        Mockito.when(server.getHost()).thenReturn("testhost");
        Mockito.when(server.getPort()).thenReturn(1234);
        Mockito.doNothing().when(server).serve(ArgumentMatchers.any(Request.class));
        final LoadBalancer loadBalancer = new LoadBalancer();
        loadBalancer.addServer(server);
        Mockito.verifyZeroInteractions(server);
        final Request request = new Request("test");
        for (int i = 0; i < ((loadBalancer.getNoOfServers()) * 2); i++) {
            loadBalancer.serverRequest(request);
        }
        Mockito.verify(server, Mockito.times(2)).serve(request);
        Mockito.verifyNoMoreInteractions(server);
    }
}

