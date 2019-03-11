/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.model.output.elastic;


import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.googlecode.jmxtrans.exceptions.LifecycleException;
import com.googlecode.jmxtrans.model.QueryFixtures;
import com.googlecode.jmxtrans.model.Result;
import com.googlecode.jmxtrans.model.Server;
import com.googlecode.jmxtrans.model.ServerFixtures;
import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ElasticWriterTests {
    private static final String PREFIX = "rootPrefix";

    @Mock(name = "jestClient")
    private JestClient mockClient;

    @Mock
    private DocumentResult jestResultTrue;

    @Mock
    private JestResult jestResultFalse;

    @InjectMocks
    private ElasticWriter writer = createElasticWriter();

    private Result result;

    @Test
    public void sendMessageToElastic() throws Exception {
        // return for call, does index exist
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(IndicesExists.class))).thenReturn(jestResultFalse);
        // return for call, is index created
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(CreateIndex.class))).thenReturn(jestResultTrue);
        // return for call, is mapping created
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(PutMapping.class))).thenReturn(jestResultTrue);
        // return for call, add index entry
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(Index.class))).thenReturn(jestResultTrue);
        // creates the index if needed
        writer.start();
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ImmutableList.of(result));
        writer.close();
        Mockito.verify(mockClient, Mockito.times(4)).execute(Matchers.<Action<JestResult>>any());
    }

    @Test
    public void sendNonNumericMessageToElastic() throws Exception {
        Result resultWithNonNumericValue = new Result(1, "attributeName", "className", "objDomain", "classNameAlias", "typeName", ImmutableList.of("key"), "abc");
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ImmutableList.of(resultWithNonNumericValue));
        // only one call is expected: the index check. No write is being made with non-numeric values.
        Mockito.verify(mockClient, Mockito.times(0)).execute(Matchers.<Action<JestResult>>any());
    }

    @Test(expected = IOException.class)
    public void sendMessageToElasticWriteThrowsException() throws Exception {
        // return for call, is index created
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(Action.class))).thenThrow(new IOException("Failed to add index entry to elastic."));
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ImmutableList.of(result));
        // only one call is expected: the insert index entry. No write is being made with non-numeric values.
        Mockito.verify(mockClient, Mockito.times(1)).execute(Matchers.<Action<JestResult>>any());
    }

    @Test(expected = ElasticWriterException.class)
    public void sendMessageToElasticWriteResultNotSucceeded() throws Exception {
        // return for call, is index created
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(jestResultFalse);
        Mockito.when(jestResultFalse.getErrorMessage()).thenReturn("Failed to add index entry to elastic.");
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ImmutableList.of(result));
        // only one call is expected: the insert index entry.
        Mockito.verify(mockClient, Mockito.times(1)).execute(Matchers.<Action<JestResult>>any());
    }

    @Test
    public void sendMessageToElasticAndVerify() throws Exception {
        Server serverWithKnownValues = ServerFixtures.serverWithAliasAndNoQuery();
        int epoch = 1123455;
        String attributeName = "attributeName123";
        String className = "className123";
        String objDomain = "objDomain123";
        String classNameAlias = "classNameAlias123";
        String typeName = "typeName123";
        String key = "myKey";
        int value = 1122;
        Result resultWithKnownValues = new Result(epoch, attributeName, className, objDomain, classNameAlias, typeName, ImmutableList.of(key), value);
        ArgumentCaptor<Index> argument = ArgumentCaptor.forClass(Index.class);
        // return for call, add index entry
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(Index.class))).thenReturn(jestResultTrue);
        writer.doWrite(serverWithKnownValues, QueryFixtures.dummyQuery(), ImmutableList.of(resultWithKnownValues));
        Mockito.verify(mockClient).execute(argument.capture());
        Assert.assertEquals(((ElasticWriterTests.PREFIX) + "_jmx-entries"), argument.getValue().getIndex());
        Gson gson = new Gson();
        String data = argument.getValue().getData(gson);
        Assert.assertTrue("Contains host", data.contains(ServerFixtures.DEFAULT_HOST));
        Assert.assertTrue("Contains port", data.contains(ServerFixtures.DEFAULT_PORT));
        Assert.assertTrue("Contains attribute name", data.contains(attributeName));
        Assert.assertTrue("Contains class name", data.contains(className));
        Assert.assertTrue("Contains object domain", data.contains(objDomain));
        Assert.assertTrue("Contains classNameAlias", data.contains(classNameAlias));
        Assert.assertTrue("Contains type name", data.contains(typeName));
        Assert.assertTrue("Contains timestamp", data.contains(String.valueOf(epoch)));
        Assert.assertTrue("Contains key", data.contains(key));
        Assert.assertTrue("Contains value", data.contains(String.valueOf(value)));
        Assert.assertTrue("Contains serverAlias", data.contains(ServerFixtures.SERVER_ALIAS));
    }

    @Test(expected = LifecycleException.class)
    public void indexCreateFailure() throws Exception {
        // return for call, does index exist
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(IndicesExists.class))).thenReturn(jestResultFalse);
        // return for call, is index created; return false
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(CreateIndex.class))).thenReturn(jestResultFalse);
        // return error message
        Mockito.when(jestResultFalse.getErrorMessage()).thenReturn("Unknown error creating index in elastic");
        // expected to throw an exception
        writer.start();
    }

    @Test(expected = LifecycleException.class)
    public void mappingCreateFailure() throws Exception {
        // return for call, does index exist
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(IndicesExists.class))).thenReturn(jestResultFalse);
        // return for call, is index created; return false
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(CreateIndex.class))).thenReturn(jestResultTrue);
        // return for call, is mapping created; return false
        Mockito.when(mockClient.execute(ArgumentMatchers.isA(PutMapping.class))).thenReturn(jestResultFalse);
        // return error message
        Mockito.when(jestResultFalse.getErrorMessage()).thenReturn("Unknown error creating mapping in elastic");
        // expected to throw an exception
        writer.start();
    }

    @Test
    public void checkToString() throws Exception {
        Assert.assertTrue(writer.toString().contains("ElasticWriter"));
    }

    @Test
    public void testValidateSetup() throws Exception {
        writer.validateSetup(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery());
        // no exception expected
    }
}

