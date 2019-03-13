/**
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.socket.handler;


import CloseStatus.NORMAL;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.socket.WebSocketSession;


/**
 * Test fixture for {@link PerConnectionWebSocketHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class PerConnectionWebSocketHandlerTests {
    @Test
    public void afterConnectionEstablished() throws Exception {
        @SuppressWarnings("resource")
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.refresh();
        PerConnectionWebSocketHandlerTests.EchoHandler.reset();
        PerConnectionWebSocketHandler handler = new PerConnectionWebSocketHandler(PerConnectionWebSocketHandlerTests.EchoHandler.class);
        handler.setBeanFactory(context.getBeanFactory());
        WebSocketSession session = new TestWebSocketSession();
        handler.afterConnectionEstablished(session);
        Assert.assertEquals(1, PerConnectionWebSocketHandlerTests.EchoHandler.initCount);
        Assert.assertEquals(0, PerConnectionWebSocketHandlerTests.EchoHandler.destroyCount);
        handler.afterConnectionClosed(session, NORMAL);
        Assert.assertEquals(1, PerConnectionWebSocketHandlerTests.EchoHandler.initCount);
        Assert.assertEquals(1, PerConnectionWebSocketHandlerTests.EchoHandler.destroyCount);
    }

    public static class EchoHandler extends AbstractWebSocketHandler implements DisposableBean {
        private static int initCount;

        private static int destroyCount;

        public EchoHandler() {
            (PerConnectionWebSocketHandlerTests.EchoHandler.initCount)++;
        }

        @Override
        public void destroy() throws Exception {
            (PerConnectionWebSocketHandlerTests.EchoHandler.destroyCount)++;
        }

        public static void reset() {
            PerConnectionWebSocketHandlerTests.EchoHandler.initCount = 0;
            PerConnectionWebSocketHandlerTests.EchoHandler.destroyCount = 0;
        }
    }
}

