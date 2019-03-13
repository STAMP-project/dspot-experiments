/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;


import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.SimpleSerializationUtil;
import org.mockitoutil.TestBase;


@SuppressWarnings("serial")
public class ObjectsSerializationTest extends TestBase implements Serializable {
    // Ok, this test has nothing to do with mocks but it shows fundamental feature of java serialization that
    // plays important role in mocking:
    // Serialization/deserialization actually replaces all instances of serialized object in the object graph (if there are any)
    // thanks to that mechanizm, stubbing & verification can correctly match method invocations because
    // one of the parts of invocation matching is checking if mock object is the same
    class Bar implements Serializable {
        ObjectsSerializationTest.Foo foo;
    }

    class Foo implements Serializable {
        ObjectsSerializationTest.Bar bar;

        Foo() {
            bar = new ObjectsSerializationTest.Bar();
            bar.foo = this;
        }
    }

    @Test
    public void shouldSerializationWork() throws Exception {
        // given
        ObjectsSerializationTest.Foo foo = new ObjectsSerializationTest.Foo();
        // when
        foo = SimpleSerializationUtil.serializeAndBack(foo);
        // then
        Assert.assertSame(foo, foo.bar.foo);
    }
}

