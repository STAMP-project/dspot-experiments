/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.agent.bootstrapper;


import AgentBootstrapperArgs.SslMode;
import com.thoughtworks.cruise.agent.common.launcher.AgentLaunchDescriptor;
import com.thoughtworks.cruise.agent.common.launcher.AgentLauncher;
import com.thoughtworks.go.util.ReflectionUtil;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AgentBootstrapperTest {
    @Test
    public void shouldNotDieWhenCreationOfLauncherRaisesException() throws InterruptedException {
        final Semaphore waitForLauncherCreation = new Semaphore(1);
        waitForLauncherCreation.acquire();
        final boolean[] reLaunchWaitIsCalled = new boolean[1];
        final AgentBootstrapper bootstrapper = new AgentBootstrapper() {
            @Override
            void waitForRelaunchTime() {
                Assert.assertThat(waitTimeBeforeRelaunch, Matchers.is(0));
                reLaunchWaitIsCalled[0] = true;
                super.waitForRelaunchTime();
            }

            @Override
            AgentLauncherCreator getLauncherCreator() {
                return new AgentLauncherCreator() {
                    public AgentLauncher createLauncher() {
                        try {
                            throw new RuntimeException("i bombed");
                        } finally {
                            if ((waitForLauncherCreation.availablePermits()) == 0) {
                                waitForLauncherCreation.release();
                            }
                        }
                    }

                    @Override
                    public void close() {
                    }
                };
            }
        };
        final AgentBootstrapper spyBootstrapper = stubJVMExit(bootstrapper);
        Thread stopLoopThd = new Thread(new Runnable() {
            public void run() {
                try {
                    waitForLauncherCreation.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ReflectionUtil.setField(spyBootstrapper, "loop", false);
            }
        });
        stopLoopThd.start();
        try {
            spyBootstrapper.go(true, new com.thoughtworks.go.agent.common.AgentBootstrapperArgs(new URL(((("http://" + ("ghost-name" + ":")) + 3518) + "/go")), null, SslMode.NONE));
            stopLoopThd.join();
        } catch (Exception e) {
            Assert.fail("should not have propagated exception thrown while creating launcher");
        }
        Assert.assertThat(reLaunchWaitIsCalled[0], Matchers.is(true));
    }

    @Test(timeout = 10 * 1000)
    public void shouldNotRelaunchAgentLauncherWhenItReturnsAnIrrecoverableCode() throws InterruptedException {
        final boolean[] destroyCalled = new boolean[1];
        final AgentBootstrapper bootstrapper = new AgentBootstrapper() {
            @Override
            AgentLauncherCreator getLauncherCreator() {
                return new AgentLauncherCreator() {
                    public AgentLauncher createLauncher() {
                        return new AgentLauncher() {
                            public int launch(AgentLaunchDescriptor descriptor) {
                                return AgentLauncher.IRRECOVERABLE_ERROR;
                            }
                        };
                    }

                    @Override
                    public void close() {
                        destroyCalled[0] = true;
                    }
                };
            }
        };
        final AgentBootstrapper spyBootstrapper = stubJVMExit(bootstrapper);
        try {
            spyBootstrapper.go(true, new com.thoughtworks.go.agent.common.AgentBootstrapperArgs(new URL(((("http://" + ("ghost-name" + ":")) + 3518) + "/go")), null, SslMode.NONE));
        } catch (Exception e) {
            Assert.fail("should not have propagated exception thrown while invoking the launcher");
        }
        Assert.assertThat(destroyCalled[0], Matchers.is(true));
    }

    @Test
    public void shouldNotDieWhenInvocationOfLauncherRaisesException_butCreationOfLauncherWentThrough() throws InterruptedException {
        final Semaphore waitForLauncherInvocation = new Semaphore(1);
        waitForLauncherInvocation.acquire();
        final AgentBootstrapper bootstrapper = new AgentBootstrapper() {
            @Override
            AgentLauncherCreator getLauncherCreator() {
                return new AgentLauncherCreator() {
                    public AgentLauncher createLauncher() {
                        return new AgentLauncher() {
                            public int launch(AgentLaunchDescriptor descriptor) {
                                try {
                                    throw new RuntimeException("fail!!! i say.");
                                } finally {
                                    if ((waitForLauncherInvocation.availablePermits()) == 0) {
                                        waitForLauncherInvocation.release();
                                    }
                                }
                            }
                        };
                    }

                    @Override
                    public void close() {
                    }
                };
            }
        };
        final AgentBootstrapper spyBootstrapper = stubJVMExit(bootstrapper);
        Thread stopLoopThd = new Thread(new Runnable() {
            public void run() {
                try {
                    waitForLauncherInvocation.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ReflectionUtil.setField(spyBootstrapper, "loop", false);
            }
        });
        stopLoopThd.start();
        try {
            spyBootstrapper.go(true, new com.thoughtworks.go.agent.common.AgentBootstrapperArgs(new URL(((("http://" + ("ghost-name" + ":")) + 3518) + "/go")), null, SslMode.NONE));
            stopLoopThd.join();
        } catch (Exception e) {
            Assert.fail("should not have propagated exception thrown while invoking the launcher");
        }
    }

    @Test
    public void shouldRetainStateAcrossLauncherInvocations() throws Exception {
        final Map expectedContext = new HashMap();
        AgentBootstrapper agentBootstrapper = new AgentBootstrapper() {
            @Override
            AgentLauncherCreator getLauncherCreator() {
                return new AgentLauncherCreator() {
                    public AgentLauncher createLauncher() {
                        return new AgentLauncher() {
                            public static final String COUNT = "count";

                            public int launch(AgentLaunchDescriptor descriptor) {
                                Map descriptorContext = descriptor.context();
                                incrementCount(descriptorContext);
                                incrementCount(expectedContext);
                                Integer expectedCount = ((Integer) (expectedContext.get(COUNT)));
                                Assert.assertThat(descriptorContext.get(COUNT), Matchers.is(expectedCount));
                                if (expectedCount > 3) {
                                    stopLooping();
                                }
                                return 0;
                            }

                            private void incrementCount(Map map) {
                                Integer currentInvocationCount = (map.containsKey(COUNT)) ? ((Integer) (map.get(COUNT))) : 0;
                                map.put(COUNT, (currentInvocationCount + 1));
                            }
                        };
                    }

                    @Override
                    public void close() {
                    }
                };
            }
        };
        AgentBootstrapper spy = stubJVMExit(agentBootstrapper);
        spy.go(true, new com.thoughtworks.go.agent.common.AgentBootstrapperArgs(new URL(((("http://" + ("localhost" + ":")) + 80) + "/go")), null, SslMode.NONE));
    }
}

