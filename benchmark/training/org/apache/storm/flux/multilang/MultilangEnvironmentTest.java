/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.flux.multilang;


import java.io.InputStream;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Sanity checks to make sure we can at least invoke the shells used.
 */
public class MultilangEnvironmentTest {
    private static final Logger LOG = LoggerFactory.getLogger(MultilangEnvironmentTest.class);

    @Test
    public void testInvokePython() throws Exception {
        String[] command = new String[]{ "python", "--version" };
        int exitVal = invokeCommand(command);
        Assert.assertEquals("Exit value for python is 0.", 0, exitVal);
    }

    @Test
    public void testInvokeNode() throws Exception {
        String[] command = new String[]{ "node", "--version" };
        int exitVal = invokeCommand(command);
        Assert.assertEquals("Exit value for node is 0.", 0, exitVal);
    }

    private static class StreamRedirect implements Runnable {
        private InputStream in;

        private OutputStream out;

        public StreamRedirect(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            try {
                int i = -1;
                while ((i = this.in.read()) != (-1)) {
                    out.write(i);
                } 
                this.in.close();
                this.out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

