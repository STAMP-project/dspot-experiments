/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.impl.serialization;


import java.io.Serializable;
import org.ehcache.spi.serialization.StatefulSerializer;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cdennis
 */
public class EnumTest {
    @Test
    public void basicInstanceSerialization() throws ClassNotFoundException {
        StatefulSerializer<Serializable> s = new CompactJavaSerializer<>(null);
        s.init(new TransientStateRepository());
        Assert.assertThat(s.read(s.serialize(People.Alice)), IsSame.sameInstance(People.Alice));
        Assert.assertThat(s.read(s.serialize(People.Bob)), IsSame.sameInstance(People.Bob));
        Assert.assertThat(s.read(s.serialize(People.Eve)), IsSame.sameInstance(People.Eve));
    }

    @Test
    public void classSerialization() throws ClassNotFoundException {
        StatefulSerializer<Serializable> s = new CompactJavaSerializer<>(null);
        s.init(new TransientStateRepository());
        Assert.assertThat(s.read(s.serialize(Enum.class)), IsSame.sameInstance(Enum.class));
        Assert.assertThat(s.read(s.serialize(Dogs.Handel.getClass())), IsSame.sameInstance(Dogs.Handel.getClass()));
        Assert.assertThat(s.read(s.serialize(Dogs.Cassie.getClass())), IsSame.sameInstance(Dogs.Cassie.getClass()));
        Assert.assertThat(s.read(s.serialize(Dogs.Penny.getClass())), IsSame.sameInstance(Dogs.Penny.getClass()));
    }

    @Test
    public void shiftingInstanceSerialization() throws ClassNotFoundException {
        StatefulSerializer<Serializable> s = new CompactJavaSerializer<>(null);
        s.init(new TransientStateRepository());
        ClassLoader wLoader = SerializerTestUtilities.createClassNameRewritingLoader(EnumTest.Foo_W.class);
        ClassLoader rLoader = SerializerTestUtilities.createClassNameRewritingLoader(EnumTest.Foo_R.class);
        Class<?> wClass = wLoader.loadClass(SerializerTestUtilities.newClassName(EnumTest.Foo_W.class));
        Class<?> rClass = rLoader.loadClass(SerializerTestUtilities.newClassName(EnumTest.Foo_R.class));
        Object[] wInstances = wClass.getEnumConstants();
        Object[] rInstances = rClass.getEnumConstants();
        SerializerTestUtilities.pushTccl(rLoader);
        try {
            for (int i = 0; i < (wInstances.length); i++) {
                Assert.assertThat(s.read(s.serialize(((Serializable) (wInstances[i])))), IsSame.sameInstance(rInstances[i]));
            }
        } finally {
            SerializerTestUtilities.popTccl();
        }
    }

    public static enum Foo_W {

        a,
        b,
        c() {
            int i = 5;
        },
        d() {
            float f = 5.0F;
        };}

    public static enum Foo_R {

        a,
        b() {
            byte b = 3;
        },
        c,
        d() {
            double d = 6.0;
        };}
}

