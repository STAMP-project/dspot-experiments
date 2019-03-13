/**
 * Copyright (C) 2015 The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.android.example.devsummit.archdemo.vo;


import com.android.example.devsummit.archdemo.util.ValidationFailedException;
import org.junit.Test;


public class PostTest {
    @Test
    public void valid() {
        Post post = new Post();
        post.setUserId(1);
        post.setClientId("dsadsa");
        post.setText("abc");
        post.setCreated(System.currentTimeMillis());
        post.validate();
    }

    @Test(expected = ValidationFailedException.class)
    public void invalidUserId() {
        Post post = new Post();
        post.setUserId((-1));
        post.setClientId("dsadsa");
        post.setText("abc");
        post.setCreated(System.currentTimeMillis());
        post.validate();
    }

    @Test(expected = ValidationFailedException.class)
    public void emptyCustomId() {
        Post post = new Post();
        post.setUserId(1);
        post.setClientId(null);
        post.setText("abc");
        post.setCreated(System.currentTimeMillis());
        post.validate();
    }

    @Test(expected = ValidationFailedException.class)
    public void emptyText() {
        Post post = new Post();
        post.setUserId(1);
        post.setClientId("dsadsa");
        post.setText(null);
        post.setCreated(System.currentTimeMillis());
        post.validate();
    }
}

