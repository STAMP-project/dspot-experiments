/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred.nativetask.handlers;


import CombinerHandler.COMBINE;
import java.io.IOException;
import org.apache.hadoop.mapred.Task.CombinerRunner;
import org.apache.hadoop.mapred.nativetask.Command;
import org.apache.hadoop.mapred.nativetask.INativeHandler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public class TestCombineHandler {
    private CombinerHandler handler;

    private INativeHandler nativeHandler;

    private BufferPusher pusher;

    private BufferPuller puller;

    private CombinerRunner combinerRunner;

    @Test
    public void testCommandDispatcherSetting() throws IOException {
        this.handler = new CombinerHandler(nativeHandler, combinerRunner, puller, pusher);
        Mockito.verify(nativeHandler, Mockito.times(1)).setCommandDispatcher(ArgumentMatchers.eq(handler));
        Mockito.verify(nativeHandler, Mockito.times(1)).setDataReceiver(ArgumentMatchers.eq(puller));
    }

    @Test
    public void testCombine() throws IOException, ClassNotFoundException, InterruptedException {
        this.handler = new CombinerHandler(nativeHandler, combinerRunner, puller, pusher);
        Assert.assertEquals(null, handler.onCall(COMBINE, null));
        handler.close();
        handler.close();
        Mockito.verify(combinerRunner, Mockito.times(1)).combine(ArgumentMatchers.eq(puller), ArgumentMatchers.eq(pusher));
        Mockito.verify(pusher, Mockito.times(1)).close();
        Mockito.verify(puller, Mockito.times(1)).close();
        Mockito.verify(nativeHandler, Mockito.times(1)).close();
    }

    @Test
    public void testOnCall() throws IOException {
        this.handler = new CombinerHandler(nativeHandler, combinerRunner, puller, pusher);
        Assert.assertEquals(null, handler.onCall(new Command((-1)), null));
    }
}

