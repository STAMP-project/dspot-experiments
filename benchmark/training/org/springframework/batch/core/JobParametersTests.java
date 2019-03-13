/**
 * Copyright 2008-2018 the original author or authors.
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
package org.springframework.batch.core;


import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.SerializationUtils;


/**
 *
 *
 * @author Lucas Ward
 * @author Dave Syer
 * @author Michael Minella
 * @author Mahmoud Ben Hassine
 */
public class JobParametersTests {
    JobParameters parameters;

    Date date1 = new Date(4321431242L);

    Date date2 = new Date(7809089900L);

    @Test
    public void testGetString() {
        Assert.assertEquals("value1", parameters.getString("string.key1"));
        Assert.assertEquals("value2", parameters.getString("string.key2"));
    }

    @Test
    public void testGetNullString() {
        parameters = new JobParameters(Collections.singletonMap("string.key1", new JobParameter(((String) (null)), true)));
        Assert.assertNull(parameters.getDate("string.key1"));
    }

    @Test
    public void testGetLong() {
        Assert.assertEquals(1L, parameters.getLong("long.key1").longValue());
        Assert.assertEquals(2L, parameters.getLong("long.key2").longValue());
    }

    @Test
    public void testGetDouble() {
        Assert.assertEquals(new Double(1.1), new Double(parameters.getDouble("double.key1")));
        Assert.assertEquals(new Double(2.2), new Double(parameters.getDouble("double.key2")));
    }

    @Test
    public void testGetDate() {
        Assert.assertEquals(date1, parameters.getDate("date.key1"));
        Assert.assertEquals(date2, parameters.getDate("date.key2"));
    }

    @Test
    public void testGetNullDate() {
        parameters = new JobParameters(Collections.singletonMap("date.key1", new JobParameter(((Date) (null)), true)));
        Assert.assertNull(parameters.getDate("date.key1"));
    }

    @Test
    public void testGetEmptyLong() {
        parameters = new JobParameters(Collections.singletonMap("long1", new JobParameter(((Long) (null)), true)));
        Assert.assertNull(parameters.getLong("long1"));
    }

    @Test
    public void testGetMissingLong() {
        Assert.assertNull(parameters.getLong("missing.long1"));
    }

    @Test
    public void testGetMissingDouble() {
        Assert.assertNull(parameters.getDouble("missing.double1"));
    }

    @Test
    public void testIsEmptyWhenEmpty() throws Exception {
        Assert.assertTrue(new JobParameters().isEmpty());
    }

    @Test
    public void testIsEmptyWhenNotEmpty() throws Exception {
        Assert.assertFalse(parameters.isEmpty());
    }

    @Test
    public void testEquals() {
        JobParameters testParameters = getNewParameters();
        Assert.assertTrue(testParameters.equals(parameters));
    }

    @Test
    public void testEqualsSelf() {
        Assert.assertTrue(parameters.equals(parameters));
    }

    @Test
    public void testEqualsDifferent() {
        Assert.assertFalse(parameters.equals(new JobParameters()));
    }

    @Test
    public void testEqualsWrongType() {
        Assert.assertFalse(parameters.equals("foo"));
    }

    @Test
    public void testEqualsNull() {
        Assert.assertFalse(parameters.equals(null));
    }

    @Test
    public void testToStringOrder() {
        Map<String, JobParameter> props = parameters.getParameters();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, JobParameter> entry : props.entrySet()) {
            stringBuilder.append(entry.toString()).append(";");
        }
        String string1 = stringBuilder.toString();
        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put("string.key2", new JobParameter("value2", true));
        parameterMap.put("string.key1", new JobParameter("value1", true));
        parameterMap.put("long.key2", new JobParameter(2L, true));
        parameterMap.put("long.key1", new JobParameter(1L, true));
        parameterMap.put("double.key2", new JobParameter(2.2, true));
        parameterMap.put("double.key1", new JobParameter(1.1, true));
        parameterMap.put("date.key2", new JobParameter(date2, true));
        parameterMap.put("date.key1", new JobParameter(date1, true));
        JobParameters testProps = new JobParameters(parameterMap);
        props = testProps.getParameters();
        stringBuilder = new StringBuilder();
        for (Map.Entry<String, JobParameter> entry : props.entrySet()) {
            stringBuilder.append(entry.toString()).append(";");
        }
        String string2 = stringBuilder.toString();
        Assert.assertEquals(string1, string2);
    }

    @Test
    public void testHashCodeEqualWhenEmpty() throws Exception {
        int code = new JobParameters().hashCode();
        Assert.assertEquals(code, new JobParameters().hashCode());
    }

    @Test
    public void testHashCodeEqualWhenNotEmpty() throws Exception {
        int code = getNewParameters().hashCode();
        Assert.assertEquals(code, parameters.hashCode());
    }

    @Test
    public void testSerialization() {
        JobParameters params = getNewParameters();
        byte[] serialized = SerializationUtils.serialize(params);
        Assert.assertEquals(params, SerializationUtils.deserialize(serialized));
    }

    @Test
    public void testLongReturnsNullWhenKeyDoesntExit() {
        Assert.assertNull(new JobParameters().getLong("keythatdoesntexist"));
    }

    @Test
    public void testStringReturnsNullWhenKeyDoesntExit() {
        Assert.assertNull(new JobParameters().getString("keythatdoesntexist"));
    }

    @Test
    public void testDoubleReturnsNullWhenKeyDoesntExit() {
        Assert.assertNull(new JobParameters().getDouble("keythatdoesntexist"));
    }

    @Test
    public void testDateReturnsNullWhenKeyDoesntExit() {
        Assert.assertNull(new JobParameters().getDate("keythatdoesntexist"));
    }
}

