/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.process.local;


import LocalProcessLauncher.ProcessContext;
import ProcessConfig.Builder;
import ProcessLog.Level;
import ProcessLog.Level.DEBUG;
import ProcessLog.Level.ERROR;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.ProcessLog;
import com.liferay.petra.process.TerminationProcessException;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.WriteAbortedException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class LocalProcessExecutorTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = new CodeCoverageAssertor() {
        @Override
        public void appendAssertClasses(List<Class<?>> assertClasses) {
            assertClasses.add(ProcessConfig.class);
            Collections.addAll(assertClasses, ProcessConfig.class.getDeclaredClasses());
            assertClasses.add(LocalProcessLauncher.class);
            Collections.addAll(assertClasses, LocalProcessLauncher.class.getDeclaredClasses());
        }
    };

    @Test
    public void testHeartBeatThreadDetachOnBrokenPipe() throws Exception {
        _testHearBeatThreadDetachByShutdownHook(LocalProcessExecutorTest.Operations.SHUTDOWN_HOOK_TRIGGER_BROKEN_PIPE, LocalProcessExecutorTest.ShutdownHooks.DETACH_ON_BROKEN_PIPE_SHUTDOWN_HOOK);
    }

    @Test
    public void testHeartBeatThreadDetachOnInterruption() throws Exception {
        _testHearBeatThreadDetachByShutdownHook(LocalProcessExecutorTest.Operations.SHUTDOWN_HOOK_TRIGGER_INTERRUPTION, LocalProcessExecutorTest.ShutdownHooks.DETACH_ON_INTERRUPTION_SHUTDOWN_HOOK);
    }

    @Test
    public void testHeartBeatThreadDetachOnUnknown() throws Exception {
        _testHearBeatThreadDetachByShutdownHook(LocalProcessExecutorTest.Operations.SHUTDOWN_HOOK_TRIGGER_UNKNOWN, LocalProcessExecutorTest.ShutdownHooks.DETACH_ON_UNKNOWN_SHUTDOWN_HOOK);
    }

    @Test
    public void testLocalProcessLauncherConstructor() {
        new LocalProcessLauncher();
    }

    @Test
    public void testProcessCallableWithException() throws Exception {
        // ProcessException
        ProcessChannel<Serializable> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1), () -> {
            throw new ProcessException("ROOT ProcessException");
        });
        NoticeableFuture<Serializable> noticeableFuture = processChannel.getProcessNoticeableFuture();
        try {
            noticeableFuture.get();
            Assert.fail();
        } catch (ExecutionException ee) {
            Throwable throwable = ee.getCause();
            Assert.assertSame(ProcessException.class, throwable.getClass());
            Assert.assertEquals("ROOT ProcessException", throwable.getMessage());
        }
        // NullPointerException
        processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1), () -> {
            throw new NullPointerException("ROOT NullPointerException");
        });
        noticeableFuture = processChannel.getProcessNoticeableFuture();
        try {
            noticeableFuture.get();
            Assert.fail();
        } catch (ExecutionException ee) {
            Throwable throwable = ee.getCause();
            Assert.assertSame(ProcessException.class, throwable.getClass());
            throwable = throwable.getCause();
            Assert.assertSame(NullPointerException.class, throwable.getClass());
            Assert.assertEquals("ROOT NullPointerException", throwable.getMessage());
        }
    }

    @Test
    public void testProcessConfigBuilderEnvironment() throws Exception {
        // Default environment
        ProcessConfig.Builder builder = new ProcessConfig.Builder();
        builder.setArguments(LocalProcessExecutorTest._createArguments(LocalProcessExecutorTest._JPDA_OPTIONS1));
        builder.setBootstrapClassPath(System.getProperty("java.class.path"));
        builder.setReactClassLoader(LocalProcessExecutorTest.class.getClassLoader());
        ProcessChannel<HashMap<String, String>> processChannel = _localProcessExecutor.execute(builder.build(), LocalProcessExecutorTest.Operations.GET_ENVIRONMENT);
        Future<HashMap<String, String>> future = processChannel.getProcessNoticeableFuture();
        Assert.assertEquals(System.getenv(), future.get());
        // New environment
        Map<String, String> environmentMap = new HashMap<>();
        environmentMap.put("key1", "value1");
        environmentMap.put("key2", "value2");
        builder.setEnvironment(environmentMap);
        processChannel = _localProcessExecutor.execute(builder.build(), LocalProcessExecutorTest.Operations.GET_ENVIRONMENT);
        future = processChannel.getProcessNoticeableFuture();
        Map<String, String> actualEnvironmentMap = future.get();
        Assert.assertEquals("value1", actualEnvironmentMap.get("key1"));
        Assert.assertEquals("value2", actualEnvironmentMap.get("key2"));
    }

    @Test
    public void testProcessConfigBuilderJavaExecutable() throws Exception {
        try {
            ProcessConfig.Builder builder = new ProcessConfig.Builder();
            builder.setJavaExecutable("javax");
            _localProcessExecutor.execute(builder.build(), LocalProcessExecutorTest.Operations.SLEEP);
            Assert.fail();
        } catch (ProcessException pe) {
            Throwable throwable = pe.getCause();
            Assert.assertTrue((throwable instanceof IOException));
        }
    }

    @Test
    public void testProcessConfigBuilderRuntimeClassPath() throws Exception {
        ProcessConfig.Builder builder = new ProcessConfig.Builder();
        builder.setArguments(LocalProcessExecutorTest._createArguments(LocalProcessExecutorTest._JPDA_OPTIONS1));
        char[] largeFileNameChars = new char[(10 * 1024) * 1024];
        largeFileNameChars[0] = CharPool.SLASH;
        for (int i = 1; i < (largeFileNameChars.length); i++) {
            largeFileNameChars[i] = ((char) ('a' + (i % 26)));
        }
        String largeFileName = new String(largeFileNameChars);
        builder.setRuntimeClassPath(largeFileName);
        ProcessChannel<String> processChannel = _localProcessExecutor.execute(builder.build(), LocalProcessExecutorTest.Operations.GET_RUNTIME_CLASS_PATH);
        Future<String> future = processChannel.getProcessNoticeableFuture();
        Assert.assertEquals(largeFileName, future.get());
    }

    @Test
    public void testProcessContextAttach() throws Exception {
        ProcessChannel<String> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1), LocalProcessExecutorTest.Operations.asControllable(LocalProcessExecutorTest.Operations.SLEEP));
        Future<LocalProcessExecutorTest.Controller> future = processChannel.write(LocalProcessExecutorTest.Operations.GET_CONTROLLER);
        LocalProcessExecutorTest.Controller parentController = future.get();
        Assert.assertTrue(parentController.isAlive());
        LocalProcessExecutorTest.Controller childController = parentController.invoke(LocalProcessExecutorTest.Operations.asNewJVM(LocalProcessExecutorTest._JPDA_OPTIONS2, LocalProcessExecutorTest.Operations.SLEEP));
        Assert.assertTrue(childController.isAlive());
        // Initially not attached
        Assert.assertFalse(childController.invoke(LocalProcessExecutorTest.Operations.IS_ATTACHED));
        // Detach is not doing anything
        Assert.assertEquals("DONE", childController.invoke(LocalProcessExecutorTest.Operations.DETACH));
        Assert.assertFalse(childController.invoke(LocalProcessExecutorTest.Operations.IS_ATTACHED));
        // Attach child to parent
        Assert.assertTrue(childController.invoke(LocalProcessExecutorTest.Operations.attach(LocalProcessExecutorTest.ShutdownHooks.TERMINATE_SHUTDOWN_HOOK)));
        Assert.assertTrue(childController.invoke(LocalProcessExecutorTest.Operations.IS_ATTACHED));
        // Double attach is rejected
        Assert.assertFalse(childController.invoke(LocalProcessExecutorTest.Operations.attach(LocalProcessExecutorTest.ShutdownHooks.TERMINATE_SHUTDOWN_HOOK)));
        // Detach
        Assert.assertEquals("DONE", childController.invoke(LocalProcessExecutorTest.Operations.DETACH));
        Assert.assertFalse(childController.invoke(LocalProcessExecutorTest.Operations.IS_ATTACHED));
        // Reattach
        Assert.assertTrue(childController.invoke(LocalProcessExecutorTest.Operations.attach(LocalProcessExecutorTest.ShutdownHooks.TERMINATE_SHUTDOWN_HOOK)));
        Assert.assertTrue(childController.invoke(LocalProcessExecutorTest.Operations.IS_ATTACHED));
        // Kill parent
        parentController.invoke(LocalProcessExecutorTest.Operations.TERMINATE);
        Assert.assertFalse(parentController.isAlive());
        NoticeableFuture<String> noticeableFuture = processChannel.getProcessNoticeableFuture();
        Assert.assertEquals("DONE", noticeableFuture.get());
        // Time wait 10 minutes to assert child is dead
        _timeWaitAssertFalse("The child process is still alive", childController::isAlive, 10, TimeUnit.MINUTES);
    }

    @Test
    public void testProcessContextAttachWithNullShutdownHook() throws Exception {
        ProcessChannel<String> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1), LocalProcessExecutorTest.Operations.asControllable(LocalProcessExecutorTest.Operations.SLEEP));
        Future<LocalProcessExecutorTest.Controller> future = processChannel.write(LocalProcessExecutorTest.Operations.GET_CONTROLLER);
        LocalProcessExecutorTest.Controller parentController = future.get();
        Assert.assertTrue(parentController.isAlive());
        LocalProcessExecutorTest.Controller childController = parentController.invoke(LocalProcessExecutorTest.Operations.asNewJVM(LocalProcessExecutorTest._JPDA_OPTIONS2, LocalProcessExecutorTest.Operations.SLEEP));
        Assert.assertTrue(childController.isAlive());
        // Attach with null shutdown hook
        Assert.assertEquals("DONE", childController.invoke(() -> {
            try {
                LocalProcessLauncher.ProcessContext.attach("NullShutdownHook", 1, null);
                return "NULL_SHUTDOWN_HOOK_ACCEPTED";
            } catch ( iae) {
                if (!("Shutdown hook is null".equals(iae.getMessage()))) {
                    return iae.getMessage();
                }
            }
            return "DONE";
        }));
        Assert.assertFalse(childController.invoke(LocalProcessExecutorTest.Operations.IS_ATTACHED));
        // Kill parent
        parentController.invoke(LocalProcessExecutorTest.Operations.TERMINATE);
        Assert.assertFalse(parentController.isAlive());
        // Kill child
        childController.invoke(LocalProcessExecutorTest.Operations.TERMINATE);
        Assert.assertFalse(childController.isAlive());
    }

    @Test
    public void testProcessContextConstructor() throws Exception {
        Constructor<LocalProcessLauncher.ProcessContext> constructor = ProcessContext.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
        Assert.assertNotNull(ProcessContext.getAttributes());
    }

    @Test
    public void testSpawnProcessWithoutAttach() throws Exception {
        ProcessChannel<String> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1), LocalProcessExecutorTest.Operations.asControllable(LocalProcessExecutorTest.Operations.SLEEP));
        Future<LocalProcessExecutorTest.Controller> future = processChannel.write(LocalProcessExecutorTest.Operations.GET_CONTROLLER);
        LocalProcessExecutorTest.Controller parentController = future.get();
        Assert.assertTrue(parentController.isAlive());
        LocalProcessExecutorTest.Controller childController = parentController.invoke(LocalProcessExecutorTest.Operations.asNewJVM(LocalProcessExecutorTest._JPDA_OPTIONS2, LocalProcessExecutorTest.Operations.SLEEP));
        Assert.assertTrue(childController.isAlive());
        // Kill parent
        parentController.invoke(LocalProcessExecutorTest.Operations.TERMINATE);
        Assert.assertFalse(parentController.isAlive());
        NoticeableFuture<String> noticeableFuture = processChannel.getProcessNoticeableFuture();
        Assert.assertEquals("DONE", noticeableFuture.get());
        // Test alive 10 times for child process
        for (int i = 0; i < 10; i++) {
            Thread.sleep(100);
            Assert.assertTrue(childController.isAlive());
        }
        // Kill child
        childController.invoke(LocalProcessExecutorTest.Operations.TERMINATE);
        Assert.assertFalse(childController.isAlive());
    }

    @Test
    public void testSubprocessReactorAbort() throws Exception {
        List<ProcessLog> processLogs = new ArrayList<>();
        ProcessConfig.Builder builder = new ProcessConfig.Builder();
        builder.setArguments(LocalProcessExecutorTest._createArguments(LocalProcessExecutorTest._JPDA_OPTIONS1));
        builder.setBootstrapClassPath(System.getProperty("java.class.path"));
        builder.setProcessLogConsumer(( processLog) -> {
            if ((processLog.getLevel()) == ProcessLog.Level.ERROR) {
                processLogs.add(processLog);
            }
        });
        builder.setReactClassLoader(new URLClassLoader(new URL[0], null));
        ProcessChannel<Boolean> processChannel = _localProcessExecutor.execute(builder.build(), LocalProcessExecutorTest.Operations.IS_ATTACHED);
        Future<Boolean> future = processChannel.getProcessNoticeableFuture();
        try {
            future.get();
            Assert.fail();
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            Assert.assertSame(ClassNotFoundException.class, cause.getClass());
        }
        Assert.assertEquals(processLogs.toString(), 1, processLogs.size());
        ProcessLog processLog = processLogs.get(0);
        Assert.assertEquals("Abort subprocess piping", processLog.getMessage());
        Throwable throwable = processLog.getThrowable();
        Assert.assertSame(ClassNotFoundException.class, throwable.getClass());
    }

    @Test
    public void testSubprocessReactorCorruptedStream() throws Exception {
        List<ProcessLog> processLogs = new ArrayList<>();
        ProcessChannel<Serializable> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1, ( processLog) -> {
            if ((processLog.getLevel()) == (Level.ERROR)) {
                processLogs.add(processLog);
            }
        }), LocalProcessExecutorTest.Operations.CORRUPTED_STREAM);
        Future<Serializable> future = processChannel.getProcessNoticeableFuture();
        try {
            future.get();
            Assert.fail();
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            Assert.assertTrue((cause instanceof ProcessException));
            Assert.assertEquals("Corrupted object input stream", cause.getMessage());
            cause = cause.getCause();
            Assert.assertSame(StreamCorruptedException.class, cause.getClass());
        }
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(processLogs.toString(), 1, processLogs.size());
        ProcessLog processLog = processLogs.get(0);
        String message = processLog.getMessage();
        int index = message.lastIndexOf(' ');
        Assert.assertTrue((index != (-1)));
        Assert.assertEquals("Dumping content of corrupted object input stream to", message.substring(0, index));
        File file = new File(message.substring((index + 1)));
        Assert.assertTrue(file.exists());
        file.delete();
        Throwable throwable = processLog.getThrowable();
        Assert.assertSame(StreamCorruptedException.class, throwable.getClass());
    }

    @Test
    public void testSubprocessReactorCrash() throws Exception {
        // One crash
        ProcessChannel<Serializable> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1), LocalProcessExecutorTest.Operations.crashJVM(1));
        Future<Serializable> future = processChannel.getProcessNoticeableFuture();
        try {
            future.get();
            Assert.fail();
        } catch (ExecutionException ee) {
            Throwable throwable = ee.getCause();
            Assert.assertSame(TerminationProcessException.class, throwable.getClass());
            Assert.assertEquals("Subprocess terminated with exit code 1", throwable.getMessage());
            TerminationProcessException terminationProcessException = ((TerminationProcessException) (throwable));
            Assert.assertEquals(1, terminationProcessException.getExitCode());
        }
        // Zero crash
        processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1), LocalProcessExecutorTest.Operations.crashJVM(0));
        future = processChannel.getProcessNoticeableFuture();
        try {
            future.get();
            Assert.fail();
        } catch (ExecutionException ee) {
            Throwable throwable = ee.getCause();
            Assert.assertSame(ProcessException.class, throwable.getClass());
            Assert.assertEquals("Subprocess piping back ended prematurely", throwable.getMessage());
            throwable = throwable.getCause();
            Assert.assertSame(EOFException.class, throwable.getClass());
        }
    }

    @Test
    public void testSubprocessReactorKillByCancel() throws Exception {
        List<ProcessLog> processLogs = new ArrayList<>();
        ProcessChannel<String> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1, ( processLog) -> {
            if ((processLog.getLevel()) == (Level.ERROR)) {
                processLogs.add(processLog);
            }
        }), LocalProcessExecutorTest.Operations.asControllable(LocalProcessExecutorTest.Operations.SLEEP));
        Future<LocalProcessExecutorTest.Controller> future = processChannel.write(LocalProcessExecutorTest.Operations.GET_CONTROLLER);
        LocalProcessExecutorTest.Controller controller = future.get();
        Assert.assertTrue(controller.isAlive());
        Map<String, Object> attributes = ProcessContext.getAttributes();
        BlockingQueue<Thread> reactorThreadBlockingQueue = new SynchronousQueue<>();
        attributes.put("reactorThreadBlockingQueue", reactorThreadBlockingQueue);
        controller.invoke(() -> {
            try {
                LocalProcessLauncher.ProcessContext.writeProcessCallable(() -> {
                    Map<String, Object> localAttributes = LocalProcessLauncher.ProcessContext.getAttributes();
                    BlockingQueue<Thread> localReactorThreadBlockingQueue = ((BlockingQueue<Thread>) (localAttributes.remove("reactorThreadBlockingQueue")));
                    try {
                        localReactorThreadBlockingQueue.put(Thread.currentThread());
                    } catch ( ie) {
                        throw new <ie>ProcessException();
                    }
                    return null;
                });
            } catch ( ioe) {
                throw new <ioe>ProcessException();
            }
            return null;
        });
        Thread reactorThread = reactorThreadBlockingQueue.take();
        NoticeableFuture<String> noticeableFuture = processChannel.getProcessNoticeableFuture();
        noticeableFuture.cancel(false);
        reactorThread.join();
        Assert.assertEquals(processLogs.toString(), 1, processLogs.size());
        ProcessLog processLog = processLogs.get(0);
        Assert.assertEquals("Abort subprocess piping", processLog.getMessage());
        Throwable throwable = processLog.getThrowable();
        Assert.assertSame(IOException.class, throwable.getClass());
    }

    @Test
    public void testSubprocessReactorKillByInterruption() throws Exception {
        ProcessChannel<String> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1), LocalProcessExecutorTest.Operations.asControllable(LocalProcessExecutorTest.Operations.SLEEP));
        Future<LocalProcessExecutorTest.Controller> future = processChannel.write(LocalProcessExecutorTest.Operations.GET_CONTROLLER);
        LocalProcessExecutorTest.Controller controller = future.get();
        Assert.assertTrue(controller.isAlive());
        Map<String, Object> attributes = ProcessContext.getAttributes();
        BlockingQueue<Thread> reactorThreadBlockingQueue = new SynchronousQueue<>();
        attributes.put("reactorThreadBlockingQueue", reactorThreadBlockingQueue);
        controller.invoke(() -> {
            try {
                LocalProcessLauncher.ProcessContext.writeProcessCallable(() -> {
                    Map<String, Object> localAttributes = LocalProcessLauncher.ProcessContext.getAttributes();
                    BlockingQueue<Thread> localReactorThreadBlockingQueue = ((BlockingQueue<Thread>) (localAttributes.remove("reactorThreadBlockingQueue")));
                    try {
                        localReactorThreadBlockingQueue.put(Thread.currentThread());
                    } catch ( ie) {
                        throw new <ie>ProcessException();
                    }
                    return null;
                });
                Object processOutputStream = ReflectionTestUtil.getFieldValue(.class, "_processOutputStream");
                ReflectionTestUtil.invoke(processOutputStream, "close", new Class<?>[0]);
            } catch ( ioe) {
                throw new <ioe>ProcessException();
            }
            return null;
        });
        Thread reactorThread = reactorThreadBlockingQueue.take();
        reactorThread.interrupt();
        NoticeableFuture<String> noticeableFuture = processChannel.getProcessNoticeableFuture();
        try {
            noticeableFuture.get();
            Assert.fail();
        } catch (ExecutionException ee) {
            Throwable throwable = ee.getCause();
            Assert.assertSame(ProcessException.class, throwable.getClass());
            Assert.assertEquals("Forcibly killed subprocess on interruption", throwable.getMessage());
            throwable = throwable.getCause();
            Assert.assertSame(InterruptedException.class, throwable.getClass());
        }
    }

    @Test
    public void testSubprocessReactorLeadingLog() throws Exception {
        List<ProcessLog> processLogs = new ArrayList<>();
        AtomicReference<ProcessLog.Level> levelReference = new AtomicReference(Level.WARN);
        Consumer<ProcessLog> processLogConsumer = ( processLog) -> {
            ProcessLog.Level level = processLog.getLevel();
            if ((level.compareTo(levelReference.get())) >= 0) {
                processLogs.add(processLog);
            }
        };
        // Warn level
        ProcessChannel<String> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1, processLogConsumer), LocalProcessExecutorTest.Operations.LEADING_LOG);
        Future<String> future = processChannel.getProcessNoticeableFuture();
        Assert.assertEquals("DONE", future.get());
        Assert.assertEquals(processLogs.toString(), 1, processLogs.size());
        ProcessLog processLog = processLogs.remove(0);
        Assert.assertEquals("Found corrupt leading log Leading log", processLog.getMessage());
        // Fine level
        levelReference.set(DEBUG);
        processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1, processLogConsumer), LocalProcessExecutorTest.Operations.LEADING_LOG);
        future = processChannel.getProcessNoticeableFuture();
        Assert.assertEquals("DONE", future.get());
        Assert.assertEquals(processLogs.toString(), 3, processLogs.size());
        processLog = processLogs.remove(0);
        Assert.assertEquals("Found corrupt leading log Leading log", processLog.getMessage());
        processLog = processLogs.remove(0);
        String message = processLog.getMessage();
        Assert.assertTrue(message, message.contains("Invoked generic process callable"));
        processLog = processLogs.remove(0);
        message = processLog.getMessage();
        Assert.assertTrue(message, message.contains("Invoked generic process callable"));
        // Severe level
        levelReference.set(ERROR);
        processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1, processLogConsumer), LocalProcessExecutorTest.Operations.LEADING_LOG);
        future = processChannel.getProcessNoticeableFuture();
        Assert.assertEquals("DONE", future.get());
        Assert.assertTrue(processLogs.toString(), processLogs.isEmpty());
    }

    @Test
    public void testSubprocessReactorPipingBackExceptionProcessCallable() throws Exception {
        List<ProcessLog> processLogs = new ArrayList<>();
        ProcessChannel<Serializable> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1, ( processLog) -> {
            if ((processLog.getLevel()) == (Level.ERROR)) {
                processLogs.add(processLog);
            }
        }), LocalProcessExecutorTest.Operations.PIPING_BACK_EXCEPTION_PROCESS_CALLABLE);
        NoticeableFuture<Serializable> noticeableFuture = processChannel.getProcessNoticeableFuture();
        Assert.assertNull(noticeableFuture.get());
        Assert.assertEquals(processLogs.toString(), 1, processLogs.size());
        ProcessLog processLog = processLogs.get(0);
        Assert.assertEquals("Unable to invoke generic process callable", processLog.getMessage());
        Throwable throwable = processLog.getThrowable();
        Assert.assertSame(ProcessException.class, throwable.getClass());
        Assert.assertEquals("Exception ProcessCallable", throwable.getMessage());
    }

    @Test
    public void testSubprocessReactorPipingBackNonprocessCallable() throws Exception {
        List<ProcessLog> processLogs = new ArrayList<>();
        ProcessChannel<Serializable> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1, ( processLog) -> {
            if ((processLog.getLevel()) == (Level.INFO)) {
                processLogs.add(processLog);
            }
        }), LocalProcessExecutorTest.Operations.PIPING_BACK_NON_PROCESS_CALLABLE);
        NoticeableFuture<Serializable> noticeableFuture = processChannel.getProcessNoticeableFuture();
        noticeableFuture.get();
        Assert.assertEquals(processLogs.toString(), 1, processLogs.size());
        ProcessLog processLog = processLogs.get(0);
        Assert.assertEquals(("Received a nonprocess callable piping back string piping back " + "object"), processLog.getMessage());
    }

    @Test
    public void testSubprocessReactorPipingBackWriteAborted() throws Exception {
        List<ProcessLog> processLogs = new ArrayList<>();
        ProcessChannel<Serializable> processChannel = _localProcessExecutor.execute(LocalProcessExecutorTest._createJPDAProcessConfig(LocalProcessExecutorTest._JPDA_OPTIONS1, ( processLog) -> {
            if ((processLog.getLevel()) == (Level.WARN)) {
                processLogs.add(processLog);
            }
        }), LocalProcessExecutorTest.Operations.PIPING_BACK_WRITE_ABORTED);
        NoticeableFuture<Serializable> noticeableFuture = processChannel.getProcessNoticeableFuture();
        try {
            noticeableFuture.get();
            Assert.fail();
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            Assert.assertSame(ProcessException.class, cause.getClass());
            cause = cause.getCause();
            Assert.assertSame(NotSerializableException.class, cause.getClass());
            Assert.assertEquals(processLogs.toString(), 1, processLogs.size());
            ProcessLog processLog = processLogs.get(0);
            Assert.assertEquals("Caught a write aborted exception", processLog.getMessage());
            cause = processLog.getThrowable();
            Assert.assertSame(WriteAbortedException.class, cause.getClass());
            cause = cause.getCause();
            Assert.assertSame(NotSerializableException.class, cause.getClass());
        }
    }

    private static final String _JPDA_OPTIONS1 = "-agentlib:jdwp=transport=dt_socket,address=8001,server=y,suspend=y";

    private static final String _JPDA_OPTIONS2 = "-agentlib:jdwp=transport=dt_socket,address=8002,server=y,suspend=y";

    private static final String _SYSTEM_PROPERTIES_QUIET = "system.properties.quiet";

    private final LocalProcessExecutor _localProcessExecutor = new LocalProcessExecutor();

    private static class Controller implements Serializable {
        public <T extends Serializable> T invoke(ProcessCallable<T> processCallable) {
            try (Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), _serverPort)) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(processCallable);
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                return ((T) (objectInputStream.readObject()));
            } catch (Exception e) {
                return ReflectionUtil.throwException(e);
            }
        }

        public boolean isAlive() {
            try {
                return invoke(() -> true);
            } catch (Exception e) {
                return false;
            }
        }

        private Controller(int serverPort) {
            _serverPort = serverPort;
        }

        private static final long serialVersionUID = 1L;

        private final int _serverPort;
    }

    private static class Operations {
        public static final ProcessCallable<Serializable> CORRUPTED_STREAM = () -> {
            UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(unsyncByteArrayOutputStream)) {
                objectOutputStream.writeObject(((ProcessCallable<String>) (() -> "DONE")));
            } catch ( e) {
                throw new <e>ProcessException();
            }
            byte[] serializedData = unsyncByteArrayOutputStream.toByteArray();
            serializedData[5] = ((byte) ((serializedData[5]) + 1));
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(FileDescriptor.out);
                fileOutputStream.write(serializedData);
                fileOutputStream.flush();
            } catch ( e) {
                throw new <e>ProcessException();
            }
            return null;
        };

        public static final ProcessCallable<String> DETACH = () -> {
            try {
                LocalProcessLauncher.ProcessContext.detach();
            } catch ( ie) {
                throw new <ie>ProcessException();
            }
            return "DONE";
        };

        public static final ProcessCallable<LocalProcessExecutorTest.Controller> GET_CONTROLLER = () -> {
            Map<String, Object> attributes = LocalProcessLauncher.ProcessContext.getAttributes();
            while (true) {
                ServerSocket serverSocket = ((ServerSocket) (attributes.get("SERVER_SOCKET")));
                if (serverSocket == null) {
                    continue;
                }
                return new com.liferay.petra.process.local.Controller(serverSocket.getLocalPort());
            } 
        };

        public static final ProcessCallable<HashMap<String, String>> GET_ENVIRONMENT = () -> new HashMap<>(System.getenv());

        public static final ProcessCallable<String> GET_RUNTIME_CLASS_PATH = () -> {
            Thread currentThread = Thread.currentThread();
            URLClassLoader urlClassLoader = ((URLClassLoader) (currentThread.getContextClassLoader()));
            URL[] urls = urlClassLoader.getURLs();
            StringBundler sb = new StringBundler((urls.length * 2));
            for (URL url : urls) {
                String path = url.getPath();
                int index = path.indexOf(":/");
                if (index != (-1)) {
                    path = path.substring((index + 1));
                }
                if (path.endsWith(StringPool.SLASH)) {
                    path = path.substring(0, ((path.length()) - 1));
                }
                sb.append(path);
                sb.append(File.pathSeparator);
            }
            if ((sb.index()) > 0) {
                sb.setIndex(((sb.index()) - 1));
            }
            return sb.toString();
        };

        public static final ProcessCallable<Boolean> IS_ATTACHED = () -> LocalProcessLauncher.ProcessContext.isAttached();

        public static final ProcessCallable<String> LEADING_LOG = () -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(FileDescriptor.out);
                fileOutputStream.write("Leading log".getBytes(StringPool.UTF8));
                fileOutputStream.flush();
                System.out.print("Body STDOUT log");
                System.out.flush();
                System.err.print("Body STDERR log");
                System.err.flush();
                // Forcibly restore System.out. This is a necessary protection
                // for code coverage. Cobertura's collector thread will output
                // to System.out after the subprocess's main thread has exited.
                // That information will be captured by the parent unit test
                // process which will cause an assert Assert.failure.
                System.setOut(new PrintStream(fileOutputStream));
            } catch ( e) {
                throw new <e>ProcessException();
            }
            return "DONE";
        };

        public static final ProcessCallable<Serializable> PIPING_BACK_EXCEPTION_PROCESS_CALLABLE = () -> {
            try {
                LocalProcessLauncher.ProcessContext.writeProcessCallable(() -> {
                    throw new ProcessException("Exception ProcessCallable");
                });
            } catch ( ioe) {
                throw new <ioe>ProcessException();
            }
            return null;
        };

        public static final ProcessCallable<Serializable> PIPING_BACK_NON_PROCESS_CALLABLE = () -> {
            try {
                UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(unsyncByteArrayOutputStream) {
                    @Override
                    protected void writeStreamHeader() {
                    }
                }) {
                    objectOutputStream.reset();
                    objectOutputStream.writeUnshared("string piping back object");
                }
                synchronized(System.out) {
                    System.out.flush();
                    OutputStream outputStream = new FileOutputStream(FileDescriptor.out);
                    outputStream.write(unsyncByteArrayOutputStream.toByteArray());
                }
            } catch ( ioe) {
                throw new <ioe>ProcessException();
            }
            return null;
        };

        public static final ProcessCallable<Serializable> PIPING_BACK_WRITE_ABORTED = () -> {
            try {
                Object obj = new Object();
                LocalProcessLauncher.ProcessContext.writeProcessCallable(() -> ((Serializable) (obj)));
            } catch ( ioe) {
                throw new <ioe>ProcessException();
            }
            return null;
        };

        public static final ProcessCallable<Serializable> SHUTDOWN_HOOK_TRIGGER_BROKEN_PIPE = () -> {
            AtomicReference<? extends Thread> heartbeatThreadReference = ReflectionTestUtil.getFieldValue(.class, "_heartbeatThreadAtomicReference");
            Thread heartBeatThread = heartbeatThreadReference.get();
            Object processOutputStream = ReflectionTestUtil.getFieldValue(.class, "_processOutputStream");
            ObjectOutputStream objectOutputStream = ReflectionTestUtil.getFieldValue(processOutputStream, "_objectOutputStream");
            try {
                ReflectionTestUtil.setFieldValue(processOutputStream, "_objectOutputStream", new ObjectOutputStream(new UnsyncByteArrayOutputStream()) {
                    @Override
                    public void flush() throws IOException {
                        ReflectionTestUtil.setFieldValue(processOutputStream, "_objectOutputStream", objectOutputStream);
                        throw new IOException();
                    }
                });
            } catch ( ioe) {
                throw new <ioe>ProcessException();
            }
            try {
                heartBeatThread.join();
            } catch ( ie) {
                throw new <ie>ProcessException();
            }
            return null;
        };

        public static final ProcessCallable<Serializable> SHUTDOWN_HOOK_TRIGGER_INTERRUPTION = () -> {
            AtomicReference<? extends Thread> heartbeatThreadReference = ReflectionTestUtil.getFieldValue(.class, "_heartbeatThreadAtomicReference");
            Thread heartBeatThread = heartbeatThreadReference.get();
            heartBeatThread.interrupt();
            try {
                heartBeatThread.join();
            } catch ( ie) {
                throw new <ie>ProcessException();
            }
            return null;
        };

        public static final ProcessCallable<Serializable> SHUTDOWN_HOOK_TRIGGER_UNKNOWN = () -> {
            AtomicReference<? extends Thread> heartbeatThreadReference = ReflectionTestUtil.getFieldValue(.class, "_heartbeatThreadAtomicReference");
            Thread heartBeatThread = heartbeatThreadReference.get();
            Object processOutputStream = ReflectionTestUtil.getFieldValue(.class, "_processOutputStream");
            ObjectOutputStream objectOutputStream = ReflectionTestUtil.getFieldValue(processOutputStream, "_objectOutputStream");
            try {
                ReflectionTestUtil.setFieldValue(processOutputStream, "_objectOutputStream", new ObjectOutputStream(new UnsyncByteArrayOutputStream()) {
                    @Override
                    public void flush() {
                        ReflectionTestUtil.setFieldValue(processOutputStream, "_objectOutputStream", objectOutputStream);
                        throw new NullPointerException();
                    }
                });
            } catch ( ioe) {
                throw new <ioe>ProcessException();
            }
            try {
                heartBeatThread.join();
            } catch ( ie) {
                throw new <ie>ProcessException();
            }
            return null;
        };

        public static final ProcessCallable<String> SLEEP = () -> {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch ( ie) {
            }
            return "DONE";
        };

        public static final ProcessCallable<Serializable> TERMINATE = LocalProcessExecutorTest::_shutdown;

        public static <T extends Serializable> ProcessCallable<T> asControllable(ProcessCallable<T> processCallable) {
            return () -> {
                Map<String, Object> attributes = LocalProcessLauncher.ProcessContext.getAttributes();
                try {
                    ServerSocketChannel serverSocketChannel = _createServerSocketChannel();
                    ServerSocket serverSocket = serverSocketChannel.socket();
                    attributes.put("SERVER_SOCKET", serverSocket);
                    Thread serverThread = new Thread(() -> {
                        while (true) {
                            try (Socket socket = serverSocket.accept()) {
                                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                                ProcessCallable<Serializable> requestProcessCallable = ((ProcessCallable<Serializable>) (objectInputStream.readObject()));
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                                objectOutputStream.writeObject(requestProcessCallable.call());
                            } catch ( cce) {
                                return;
                            } catch ( e) {
                                e.printStackTrace();
                                System.exit(10);
                            }
                        } 
                    }, ((processCallable.toString()) + "-Controller-Server"));
                    serverThread.start();
                    return processCallable.call();
                } catch ( ioe) {
                    throw new <ioe>ProcessException();
                }
            };
        }

        public static ProcessCallable<LocalProcessExecutorTest.Controller> asNewJVM(String jpdaOption, ProcessCallable<?> processCallable) {
            return () -> {
                ProcessExecutor processExecutor = new LocalProcessExecutor();
                try {
                    ProcessChannel<?> processChannel = processExecutor.execute(_createJPDAProcessConfig(jpdaOption), asControllable(processCallable));
                    Future<com.liferay.petra.process.local.Controller> childControllerFuture = processChannel.write(Operations.GET_CONTROLLER);
                    return childControllerFuture.get();
                } catch ( e) {
                    throw new <e>ProcessException();
                }
            };
        }

        public static ProcessCallable<Boolean> attach(LocalProcessLauncher.ShutdownHook shutdownHook) {
            return () -> LocalProcessLauncher.ProcessContext.attach("Child Process", 1, shutdownHook);
        }

        public static ProcessCallable<Serializable> crashJVM(int exitCode) {
            return () -> {
                System.exit(exitCode);
                return null;
            };
        }
    }

    private static class ShutdownHooks {
        public static final LocalProcessExecutorTest.ShutdownHooks.SerializableShutdownHook DETACH_ON_BROKEN_PIPE_SHUTDOWN_HOOK = ( shutdownCode, shutdownThrowable) -> {
            if ((shutdownCode == LocalProcessLauncher.ShutdownHook.BROKEN_PIPE_CODE) && (shutdownThrowable instanceof IOException)) {
                _unregisterHeartBeatThread();
                return true;
            }
            return false;
        };

        public static final LocalProcessExecutorTest.ShutdownHooks.SerializableShutdownHook DETACH_ON_INTERRUPTION_SHUTDOWN_HOOK = ( shutdownCode, shutdownThrowable) -> {
            if ((shutdownCode == LocalProcessLauncher.ShutdownHook.INTERRUPTION_CODE) && ((shutdownThrowable.getClass()) == (.class))) {
                _unregisterHeartBeatThread();
                return true;
            }
            return false;
        };

        public static final LocalProcessExecutorTest.ShutdownHooks.SerializableShutdownHook DETACH_ON_UNKNOWN_SHUTDOWN_HOOK = ( shutdownCode, shutdownThrowable) -> {
            if (((shutdownCode == LocalProcessLauncher.ShutdownHook.UNKNOWN_CODE) && (!(shutdownThrowable instanceof InterruptedException))) && (!(shutdownThrowable instanceof IOException))) {
                _unregisterHeartBeatThread();
                return true;
            }
            return false;
        };

        public static final LocalProcessExecutorTest.ShutdownHooks.SerializableShutdownHook TERMINATE_SHUTDOWN_HOOK = ( shutdownCode, shutdownThrowable) -> {
            _shutdown();
            return true;
        };

        private static void _unregisterHeartBeatThread() {
            AtomicReference<? extends Thread> heartbeatThreadReference = ReflectionTestUtil.getFieldValue(ProcessContext.class, "_heartbeatThreadAtomicReference");
            heartbeatThreadReference.set(null);
        }

        private interface SerializableShutdownHook extends LocalProcessLauncher.ShutdownHook , Serializable {}
    }
}

