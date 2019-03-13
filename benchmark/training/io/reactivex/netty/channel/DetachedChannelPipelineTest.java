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
package io.reactivex.netty.channel;


import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.reactivex.netty.channel.DetachedChannelPipeline.HandlerHolder;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import rx.functions.Func0;


public class DetachedChannelPipelineTest {
    private static final EventLoopGroup MULTI_GRP = new NioEventLoopGroup();

    public static final Func0<ChannelHandler> HANDLER_FACTORY = new Func0<ChannelHandler>() {
        @Override
        public ChannelHandler call() {
            return new ChannelDuplexHandler();
        }
    };

    private static final HandlerHolder HANDLER_1_NO_NAME = new HandlerHolder(DetachedChannelPipelineTest.HANDLER_FACTORY);

    private static final HandlerHolder HANDLER_1 = new HandlerHolder("handler-1", DetachedChannelPipelineTest.HANDLER_FACTORY);

    private static final HandlerHolder HANDLER_1_GRP = new HandlerHolder("handler-1", DetachedChannelPipelineTest.HANDLER_FACTORY, new NioEventLoopGroup());

    private static final HandlerHolder HANDLER_1_GRP_NO_NAME = new HandlerHolder(null, DetachedChannelPipelineTest.HANDLER_FACTORY, DetachedChannelPipelineTest.MULTI_GRP);

    private static final HandlerHolder HANDLER_2_NO_NAME = new HandlerHolder(DetachedChannelPipelineTest.HANDLER_FACTORY);

    private static final HandlerHolder HANDLER_2 = new HandlerHolder("handler-2", DetachedChannelPipelineTest.HANDLER_FACTORY);

    private static final HandlerHolder HANDLER_2_GRP = new HandlerHolder("handler-2", DetachedChannelPipelineTest.HANDLER_FACTORY, new NioEventLoopGroup());

    private static final HandlerHolder HANDLER_2_GRP_NO_NAME = new HandlerHolder(null, DetachedChannelPipelineTest.HANDLER_FACTORY, DetachedChannelPipelineTest.MULTI_GRP);

    @Rule
    public final DetachedChannelPipelineTest.PipelineRule pipelineRule = new DetachedChannelPipelineTest.PipelineRule();

    @Test(timeout = 60000)
    public void testCopy() throws Exception {
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_1.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_1.getHandlerFactoryIfConfigured());
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_2.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2.getHandlerFactoryIfConfigured());
        DetachedChannelPipeline copy = pipelineRule.pipeline.copy();
        MatcherAssert.assertThat("Copy did not create a new instance.", copy, not(pipelineRule.pipeline));
        MatcherAssert.assertThat("Unexpected handlers count in the copy.", copy.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers in the copy.", copy.getHoldersInOrder(), contains(pipelineRule.pipeline.getHoldersInOrder().toArray()));
    }

    @Test(timeout = 60000)
    public void testAddFirst() throws Exception {
        pipelineRule.pipeline.addFirst(DetachedChannelPipelineTest.HANDLER_1.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_1.getHandlerFactoryIfConfigured());
        pipelineRule.pipeline.addFirst(DetachedChannelPipelineTest.HANDLER_2.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_2, DetachedChannelPipelineTest.HANDLER_1));
    }

    @Test(timeout = 60000)
    public void testAddFirstWithGroup() throws Exception {
        pipelineRule.pipeline.addFirst(DetachedChannelPipelineTest.HANDLER_1_GRP.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getHandlerFactoryIfConfigured());
        pipelineRule.pipeline.addFirst(DetachedChannelPipelineTest.HANDLER_2_GRP.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_2_GRP, DetachedChannelPipelineTest.HANDLER_1_GRP));
    }

    @Test(timeout = 60000)
    public void testAddLast() throws Exception {
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_1.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_1.getHandlerFactoryIfConfigured());
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_2.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_1, DetachedChannelPipelineTest.HANDLER_2));
    }

    @Test(timeout = 60000)
    public void testAddLastWithGroup() throws Exception {
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_1_GRP.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getHandlerFactoryIfConfigured());
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_2_GRP.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_1_GRP, DetachedChannelPipelineTest.HANDLER_2_GRP));
    }

    @Test(timeout = 60000)
    public void testAddBefore() throws Exception {
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_1.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_1.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_1.getHandlerFactoryIfConfigured());
        pipelineRule.pipeline.addBefore(DetachedChannelPipelineTest.HANDLER_1.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_2, DetachedChannelPipelineTest.HANDLER_1));
    }

    @Test(timeout = 60000)
    public void testAddBeforeWithGroup() throws Exception {
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_1_GRP.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getHandlerFactoryIfConfigured());
        pipelineRule.pipeline.addBefore(DetachedChannelPipelineTest.HANDLER_2_GRP.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_2_GRP, DetachedChannelPipelineTest.HANDLER_1_GRP));
    }

    @Test(timeout = 60000)
    public void testAddAfter() throws Exception {
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_1.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_1.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_1.getHandlerFactoryIfConfigured());
        pipelineRule.pipeline.addAfter(DetachedChannelPipelineTest.HANDLER_1.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_1, DetachedChannelPipelineTest.HANDLER_2));
    }

    @Test(timeout = 60000)
    public void testAddAfterWithGroup() throws Exception {
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_1_GRP.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getHandlerFactoryIfConfigured());
        pipelineRule.pipeline.addAfter(DetachedChannelPipelineTest.HANDLER_2_GRP.getGroupIfConfigured(), DetachedChannelPipelineTest.HANDLER_1_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP.getNameIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_1_GRP, DetachedChannelPipelineTest.HANDLER_2_GRP));
    }

    @Test(timeout = 60000)
    public void testAddFirstMulti() throws Exception {
        pipelineRule.pipeline.addFirst(DetachedChannelPipelineTest.HANDLER_1_NO_NAME.getHandlerFactoryIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_NO_NAME.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_1_NO_NAME, DetachedChannelPipelineTest.HANDLER_2_NO_NAME));
    }

    @Test(timeout = 60000)
    public void testAddFirstMultiWithGroup() throws Exception {
        pipelineRule.pipeline.addFirst(DetachedChannelPipelineTest.MULTI_GRP, DetachedChannelPipelineTest.HANDLER_1_GRP_NO_NAME.getHandlerFactoryIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP_NO_NAME.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_1_GRP_NO_NAME, DetachedChannelPipelineTest.HANDLER_2_GRP_NO_NAME));
    }

    @Test(timeout = 60000)
    public void testAddLastMulti() throws Exception {
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.HANDLER_1_NO_NAME.getHandlerFactoryIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_NO_NAME.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_1_NO_NAME, DetachedChannelPipelineTest.HANDLER_2_NO_NAME));
    }

    @Test(timeout = 60000)
    public void testAddLastMultiWithGroup() throws Exception {
        pipelineRule.pipeline.addLast(DetachedChannelPipelineTest.MULTI_GRP, DetachedChannelPipelineTest.HANDLER_1_GRP_NO_NAME.getHandlerFactoryIfConfigured(), DetachedChannelPipelineTest.HANDLER_2_GRP_NO_NAME.getHandlerFactoryIfConfigured());
        MatcherAssert.assertThat("Unexpected handlers count.", pipelineRule.pipeline.getHoldersInOrder(), hasSize(2));
        MatcherAssert.assertThat("Unexpected handlers.", pipelineRule.pipeline.getHoldersInOrder(), contains(DetachedChannelPipelineTest.HANDLER_1_GRP_NO_NAME, DetachedChannelPipelineTest.HANDLER_2_GRP_NO_NAME));
    }

    public static class PipelineRule extends ExternalResource {
        private DetachedChannelPipeline pipeline;

        private ChannelDuplexHandler tail;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    tail = new ChannelDuplexHandler();
                    pipeline = new DetachedChannelPipeline(new rx.functions.Action1<ChannelPipeline>() {
                        @Override
                        public void call(ChannelPipeline pipeline1) {
                            pipeline1.addLast(tail);
                        }
                    });
                    base.evaluate();
                }
            };
        }
    }
}

