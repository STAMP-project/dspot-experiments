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
package org.apache.beam.sdk.io.tika;


import org.apache.tika.metadata.Metadata;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link ParseResult}.
 */
public class ParseResultTest {
    @Test
    public void testEqualsAndHashCode() {
        ParseResult successBase = ParseResult.success("a.txt", "hello", ParseResultTest.getMetadata());
        ParseResult successSame = ParseResult.success("a.txt", "hello", ParseResultTest.getMetadata());
        ParseResult successDifferentName = ParseResult.success("b.txt", "hello", ParseResultTest.getMetadata());
        ParseResult successDifferentContent = ParseResult.success("a.txt", "goodbye", ParseResultTest.getMetadata());
        ParseResult successDifferentMetadata = ParseResult.success("a.txt", "hello", new Metadata());
        RuntimeException oops = new RuntimeException("oops");
        ParseResult failureBase = ParseResult.failure("a.txt", "", new Metadata(), oops);
        ParseResult failureSame = ParseResult.failure("a.txt", "", new Metadata(), oops);
        ParseResult failureDifferentName = ParseResult.failure("b.txt", "", new Metadata(), oops);
        ParseResult failureDifferentContent = ParseResult.failure("b.txt", "partial", new Metadata(), oops);
        ParseResult failureDifferentMetadata = ParseResult.failure("b.txt", "", ParseResultTest.getMetadata(), oops);
        ParseResult failureDifferentError = ParseResult.failure("a.txt", "", new Metadata(), new RuntimeException("eek"));
        Assert.assertEquals(successBase, successSame);
        Assert.assertEquals(successBase.hashCode(), successSame.hashCode());
        Assert.assertThat(successDifferentName, CoreMatchers.not(CoreMatchers.equalTo(successBase)));
        Assert.assertThat(successDifferentContent, CoreMatchers.not(CoreMatchers.equalTo(successBase)));
        Assert.assertThat(successDifferentMetadata, CoreMatchers.not(CoreMatchers.equalTo(successBase)));
        Assert.assertThat(successDifferentName.hashCode(), CoreMatchers.not(CoreMatchers.equalTo(successBase.hashCode())));
        Assert.assertThat(successDifferentContent.hashCode(), CoreMatchers.not(CoreMatchers.equalTo(successBase.hashCode())));
        Assert.assertThat(successDifferentMetadata.hashCode(), CoreMatchers.not(CoreMatchers.equalTo(successBase.hashCode())));
        Assert.assertThat(failureBase, CoreMatchers.not(CoreMatchers.equalTo(successBase)));
        Assert.assertThat(successBase, CoreMatchers.not(CoreMatchers.equalTo(failureBase)));
        Assert.assertEquals(failureBase, failureSame);
        Assert.assertEquals(failureBase.hashCode(), failureSame.hashCode());
        Assert.assertThat(failureDifferentName, CoreMatchers.not(CoreMatchers.equalTo(failureBase)));
        Assert.assertThat(failureDifferentError, CoreMatchers.not(CoreMatchers.equalTo(failureBase)));
        Assert.assertThat(failureDifferentContent, CoreMatchers.not(CoreMatchers.equalTo(failureBase)));
        Assert.assertThat(failureDifferentMetadata, CoreMatchers.not(CoreMatchers.equalTo(failureBase)));
        Assert.assertThat(failureDifferentName.hashCode(), CoreMatchers.not(CoreMatchers.equalTo(failureBase.hashCode())));
        Assert.assertThat(failureDifferentError.hashCode(), CoreMatchers.not(CoreMatchers.equalTo(failureBase.hashCode())));
        Assert.assertThat(failureDifferentContent.hashCode(), CoreMatchers.not(CoreMatchers.equalTo(failureBase.hashCode())));
        Assert.assertThat(failureDifferentMetadata.hashCode(), CoreMatchers.not(CoreMatchers.equalTo(failureBase.hashCode())));
    }
}

