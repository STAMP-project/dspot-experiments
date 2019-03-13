/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class ParseSessionTest {
    @Test
    public void testImmutableKeys() {
        String[] immutableKeys = new String[]{ "sessionToken", "createdWith", "restricted", "user", "expiresAt", "installationId" };
        ParseSession session = new ParseSession();
        session.put("foo", "bar");
        session.put("USER", "bar");
        session.put("_user", "bar");
        session.put("token", "bar");
        for (String immutableKey : immutableKeys) {
            try {
                session.put(immutableKey, "blah");
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("Cannot modify"));
            }
            try {
                session.remove(immutableKey);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("Cannot modify"));
            }
            try {
                session.removeAll(immutableKey, Collections.emptyList());
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("Cannot modify"));
            }
        }
    }
}

