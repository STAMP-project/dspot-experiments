/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.security;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.util.security.Credential;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;


@ExtendWith(WorkDirExtension.class)
public class PropertyUserStoreTest {
    private final class UserCount implements PropertyUserStore.UserListener {
        private final AtomicInteger userCount = new AtomicInteger();

        private final List<String> users = new ArrayList<String>();

        private UserCount() {
        }

        @Override
        public void update(String username, Credential credential, String[] roleArray) {
            if (!(users.contains(username))) {
                users.add(username);
                userCount.getAndIncrement();
            }
        }

        @Override
        public void remove(String username) {
            users.remove(username);
            userCount.getAndDecrement();
        }

        public void awaitCount(int expectedCount) throws InterruptedException {
            long timeout = (TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) + (TimeUnit.SECONDS.toMillis(10));
            while (((userCount.get()) != expectedCount) && ((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) < timeout)) {
                TimeUnit.MILLISECONDS.sleep(100);
            } 
            assertThatCount(Matchers.is(expectedCount));
        }

        public void assertThatCount(Matcher<Integer> matcher) {
            MatcherAssert.assertThat("User count", userCount.get(), matcher);
        }

        public void assertThatUsers(Matcher<Iterable<? super String>> matcher) {
            MatcherAssert.assertThat("Users list", users, matcher);
        }
    }

    public WorkDir testdir;

    @Test
    public void testPropertyUserStoreLoad() throws Exception {
        testdir.ensureEmpty();
        final PropertyUserStoreTest.UserCount userCount = new PropertyUserStoreTest.UserCount();
        final Path usersFile = initUsersText();
        PropertyUserStore store = new PropertyUserStore();
        store.setConfigFile(usersFile.toFile());
        store.registerUserListener(userCount);
        store.start();
        MatcherAssert.assertThat("Failed to retrieve UserIdentity directly from PropertyUserStore", store.getUserIdentity("tom"), Matchers.notNullValue());
        MatcherAssert.assertThat("Failed to retrieve UserIdentity directly from PropertyUserStore", store.getUserIdentity("dick"), Matchers.notNullValue());
        MatcherAssert.assertThat("Failed to retrieve UserIdentity directly from PropertyUserStore", store.getUserIdentity("harry"), Matchers.notNullValue());
        userCount.assertThatCount(Matchers.is(3));
        userCount.awaitCount(3);
    }

    @Test
    public void testPropertyUserStoreFails() throws Exception {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            PropertyUserStore store = new PropertyUserStore();
            store.setConfig("file:/this/file/does/not/exist.txt");
            store.start();
        });
    }

    @Test
    public void testPropertyUserStoreLoadFromJarFile() throws Exception {
        testdir.ensureEmpty();
        final PropertyUserStoreTest.UserCount userCount = new PropertyUserStoreTest.UserCount();
        final String usersFile = initUsersPackedFileText();
        PropertyUserStore store = new PropertyUserStore();
        store.setConfig(usersFile);
        store.registerUserListener(userCount);
        store.start();
        // 
        MatcherAssert.assertThat("Failed to retrieve UserIdentity directly from PropertyUserStore", store.getUserIdentity("tom"), Matchers.notNullValue());
        // 
        MatcherAssert.assertThat("Failed to retrieve UserIdentity directly from PropertyUserStore", store.getUserIdentity("dick"), Matchers.notNullValue());
        // 
        MatcherAssert.assertThat("Failed to retrieve UserIdentity directly from PropertyUserStore", store.getUserIdentity("harry"), Matchers.notNullValue());
        userCount.assertThatCount(Matchers.is(3));
        userCount.awaitCount(3);
    }

    @Test
    @DisabledOnOs(OS.MAC)
    public void testPropertyUserStoreLoadUpdateUser() throws Exception {
        testdir.ensureEmpty();
        final PropertyUserStoreTest.UserCount userCount = new PropertyUserStoreTest.UserCount();
        final Path usersFile = initUsersText();
        final AtomicInteger loadCount = new AtomicInteger(0);
        PropertyUserStore store = new PropertyUserStore() {
            @Override
            protected void loadUsers() throws IOException {
                loadCount.incrementAndGet();
                super.loadUsers();
            }
        };
        store.setHotReload(true);
        store.setConfigFile(usersFile.toFile());
        store.registerUserListener(userCount);
        store.start();
        userCount.assertThatCount(Matchers.is(3));
        MatcherAssert.assertThat(loadCount.get(), Matchers.is(1));
        addAdditionalUser(usersFile, "skip: skip, roleA\n");
        userCount.awaitCount(4);
        MatcherAssert.assertThat(loadCount.get(), Matchers.is(2));
        MatcherAssert.assertThat(store.getUserIdentity("skip"), Matchers.notNullValue());
        userCount.assertThatCount(Matchers.is(4));
        userCount.assertThatUsers(Matchers.hasItem("skip"));
        if (OS.LINUX.isCurrentOs())
            Files.createFile(testdir.getPath().toRealPath().resolve("unrelated.txt"), PosixFilePermissions.asFileAttribute(EnumSet.noneOf(PosixFilePermission.class)));
        else
            Files.createFile(testdir.getPath().toRealPath().resolve("unrelated.txt"));

        Thread.sleep(1100);
        MatcherAssert.assertThat(loadCount.get(), Matchers.is(2));
        userCount.assertThatCount(Matchers.is(4));
        userCount.assertThatUsers(Matchers.hasItem("skip"));
    }

    // File is locked on OS, cannot change.
    @Test
    @DisabledOnOs({ OS.MAC, OS.WINDOWS })
    public void testPropertyUserStoreLoadRemoveUser() throws Exception {
        testdir.ensureEmpty();
        final PropertyUserStoreTest.UserCount userCount = new PropertyUserStoreTest.UserCount();
        // initial user file (3) users
        final Path usersFile = initUsersText();
        // adding 4th user
        addAdditionalUser(usersFile, "skip: skip, roleA\n");
        PropertyUserStore store = new PropertyUserStore();
        store.setHotReload(true);
        store.setConfigFile(usersFile.toFile());
        store.registerUserListener(userCount);
        store.start();
        userCount.assertThatCount(Matchers.is(4));
        // rewrite file with original 3 users
        initUsersText();
        userCount.awaitCount(3);
    }
}

