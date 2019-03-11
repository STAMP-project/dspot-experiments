/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.zeppelin.geode;


import Code.ERROR;
import FormType.SIMPLE;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Properties;
import org.apache.geode.cache.query.Struct;
import org.apache.geode.cache.query.internal.types.StructTypeImpl;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.internal.PdxType;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class GeodeOqlInterpreterTest {
    private static final String OQL_QUERY = "select * from /region";

    @Test
    public void testOpenCommandIndempotency() {
        Properties properties = new Properties();
        properties.put("geode.locator.host", "localhost");
        properties.put("geode.locator.port", "10334");
        properties.put("geode.max.result", "1000");
        GeodeOqlInterpreter spyGeodeOqlInterpreter = Mockito.spy(new GeodeOqlInterpreter(properties));
        // Ensure that an attempt to open new connection will clean any remaining connections
        spyGeodeOqlInterpreter.open();
        spyGeodeOqlInterpreter.open();
        spyGeodeOqlInterpreter.open();
        Mockito.verify(spyGeodeOqlInterpreter, Mockito.times(3)).open();
        Mockito.verify(spyGeodeOqlInterpreter, Mockito.times(3)).close();
    }

    @Test
    public void oqlNumberResponse() throws Exception {
        testOql(GeodeOqlInterpreterTest.asIterator(66, 67), "Result\n66\n67\n", 10);
        testOql(GeodeOqlInterpreterTest.asIterator(66, 67), "Result\n66\n", 1);
    }

    @Test
    public void oqlStructResponse() throws Exception {
        String[] fields = new String[]{ "field1", "field2" };
        Struct s1 = new org.apache.geode.cache.query.internal.StructImpl(new StructTypeImpl(fields), new String[]{ "val11", "val12" });
        Struct s2 = new org.apache.geode.cache.query.internal.StructImpl(new StructTypeImpl(fields), new String[]{ "val21", "val22" });
        testOql(GeodeOqlInterpreterTest.asIterator(s1, s2), "field1\tfield2\t\nval11\tval12\t\nval21\tval22\t\n", 10);
        testOql(GeodeOqlInterpreterTest.asIterator(s1, s2), "field1\tfield2\t\nval11\tval12\t\n", 1);
    }

    @Test
    public void oqlStructResponseWithReservedCharacters() throws Exception {
        String[] fields = new String[]{ "fi\teld1", "f\nield2" };
        Struct s1 = new org.apache.geode.cache.query.internal.StructImpl(new StructTypeImpl(fields), new String[]{ "v\nal\t1", "val2" });
        testOql(GeodeOqlInterpreterTest.asIterator(s1), "fi eld1\tf ield2\t\nv al 1\tval2\t\n", 10);
    }

    @Test
    public void oqlPdxInstanceResponse() throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream("koza\tboza\n".getBytes());
        PdxInstance pdx1 = new org.apache.geode.pdx.internal.PdxInstanceImpl(new PdxType(), new DataInputStream(bais), 4);
        PdxInstance pdx2 = new org.apache.geode.pdx.internal.PdxInstanceImpl(new PdxType(), new DataInputStream(bais), 4);
        testOql(GeodeOqlInterpreterTest.asIterator(pdx1, pdx2), "\n", 10);
        testOql(GeodeOqlInterpreterTest.asIterator(pdx1, pdx2), "\n", 1);
    }

    private static class DummyUnspportedType {
        @Override
        public String toString() {
            return "Unsupported Indeed";
        }
    }

    @Test
    public void oqlUnsupportedTypeResponse() throws Exception {
        GeodeOqlInterpreterTest.DummyUnspportedType unspported1 = new GeodeOqlInterpreterTest.DummyUnspportedType();
        GeodeOqlInterpreterTest.DummyUnspportedType unspported2 = new GeodeOqlInterpreterTest.DummyUnspportedType();
        testOql(GeodeOqlInterpreterTest.asIterator(unspported1, unspported2), (((("Unsuppoted Type\n" + (unspported1.toString())) + "\n") + (unspported1.toString())) + "\n"), 10);
    }

    @Test
    public void oqlWithQueryException() throws Exception {
        GeodeOqlInterpreter spyGeodeOqlInterpreter = Mockito.spy(new GeodeOqlInterpreter(new Properties()));
        Mockito.when(spyGeodeOqlInterpreter.getExceptionOnConnect()).thenReturn(new RuntimeException("Test Exception On Connect"));
        InterpreterResult interpreterResult = spyGeodeOqlInterpreter.interpret(GeodeOqlInterpreterTest.OQL_QUERY, null);
        Assert.assertEquals(ERROR, interpreterResult.code());
        Assert.assertEquals("Test Exception On Connect", interpreterResult.message().get(0).getData());
    }

    @Test
    public void oqlWithExceptionOnConnect() throws Exception {
        GeodeOqlInterpreter spyGeodeOqlInterpreter = Mockito.spy(new GeodeOqlInterpreter(new Properties()));
        Mockito.when(spyGeodeOqlInterpreter.getQueryService()).thenThrow(new RuntimeException("Expected Test Exception!"));
        InterpreterResult interpreterResult = spyGeodeOqlInterpreter.interpret(GeodeOqlInterpreterTest.OQL_QUERY, null);
        Assert.assertEquals(ERROR, interpreterResult.code());
        Assert.assertEquals("Expected Test Exception!", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testFormType() {
        Assert.assertEquals(SIMPLE, new GeodeOqlInterpreter(new Properties()).getFormType());
    }
}

