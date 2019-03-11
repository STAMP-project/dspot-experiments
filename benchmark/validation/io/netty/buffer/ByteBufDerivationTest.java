/**
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.buffer;


import java.nio.ByteOrder;
import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests wrapping a wrapped buffer does not go way too deep chaining.
 */
public class ByteBufDerivationTest {
    @Test
    public void testSlice() throws Exception {
        ByteBuf buf = Unpooled.buffer(8).setIndex(1, 7);
        ByteBuf slice = buf.slice(1, 7);
        Assert.assertThat(slice, instanceOf(AbstractUnpooledSlicedByteBuf.class));
        Assert.assertThat(slice.unwrap(), Matchers.sameInstance(buf));
        Assert.assertThat(slice.readerIndex(), is(0));
        Assert.assertThat(slice.writerIndex(), is(7));
        Assert.assertThat(slice.capacity(), is(7));
        Assert.assertThat(slice.maxCapacity(), is(7));
        slice.setIndex(1, 6);
        Assert.assertThat(buf.readerIndex(), is(1));
        Assert.assertThat(buf.writerIndex(), is(7));
    }

    @Test
    public void testSliceOfSlice() throws Exception {
        ByteBuf buf = Unpooled.buffer(8);
        ByteBuf slice = buf.slice(1, 7);
        ByteBuf slice2 = slice.slice(0, 6);
        Assert.assertThat(slice2, not(Matchers.sameInstance(slice)));
        Assert.assertThat(slice2, instanceOf(AbstractUnpooledSlicedByteBuf.class));
        Assert.assertThat(slice2.unwrap(), Matchers.sameInstance(buf));
        Assert.assertThat(slice2.writerIndex(), is(6));
        Assert.assertThat(slice2.capacity(), is(6));
    }

    @Test
    public void testDuplicate() throws Exception {
        ByteBuf buf = Unpooled.buffer(8).setIndex(1, 7);
        ByteBuf dup = buf.duplicate();
        Assert.assertThat(dup, instanceOf(DuplicatedByteBuf.class));
        Assert.assertThat(dup.unwrap(), Matchers.sameInstance(buf));
        Assert.assertThat(dup.readerIndex(), is(buf.readerIndex()));
        Assert.assertThat(dup.writerIndex(), is(buf.writerIndex()));
        Assert.assertThat(dup.capacity(), is(buf.capacity()));
        Assert.assertThat(dup.maxCapacity(), is(buf.maxCapacity()));
        dup.setIndex(2, 6);
        Assert.assertThat(buf.readerIndex(), is(1));
        Assert.assertThat(buf.writerIndex(), is(7));
    }

    @Test
    public void testDuplicateOfDuplicate() throws Exception {
        ByteBuf buf = Unpooled.buffer(8).setIndex(1, 7);
        ByteBuf dup = buf.duplicate().setIndex(2, 6);
        ByteBuf dup2 = dup.duplicate();
        Assert.assertThat(dup2, not(Matchers.sameInstance(dup)));
        Assert.assertThat(dup2, instanceOf(DuplicatedByteBuf.class));
        Assert.assertThat(dup2.unwrap(), Matchers.sameInstance(buf));
        Assert.assertThat(dup2.readerIndex(), is(dup.readerIndex()));
        Assert.assertThat(dup2.writerIndex(), is(dup.writerIndex()));
        Assert.assertThat(dup2.capacity(), is(dup.capacity()));
        Assert.assertThat(dup2.maxCapacity(), is(dup.maxCapacity()));
    }

    @Test
    public void testReadOnly() throws Exception {
        ByteBuf buf = Unpooled.buffer(8).setIndex(1, 7);
        ByteBuf ro = Unpooled.unmodifiableBuffer(buf);
        Assert.assertThat(ro, instanceOf(ReadOnlyByteBuf.class));
        Assert.assertThat(ro.unwrap(), Matchers.sameInstance(buf));
        Assert.assertThat(ro.readerIndex(), is(buf.readerIndex()));
        Assert.assertThat(ro.writerIndex(), is(buf.writerIndex()));
        Assert.assertThat(ro.capacity(), is(buf.capacity()));
        Assert.assertThat(ro.maxCapacity(), is(buf.maxCapacity()));
        ro.setIndex(2, 6);
        Assert.assertThat(buf.readerIndex(), is(1));
    }

    @Test
    public void testReadOnlyOfReadOnly() throws Exception {
        ByteBuf buf = Unpooled.buffer(8).setIndex(1, 7);
        ByteBuf ro = Unpooled.unmodifiableBuffer(buf).setIndex(2, 6);
        ByteBuf ro2 = Unpooled.unmodifiableBuffer(ro);
        Assert.assertThat(ro2, not(Matchers.sameInstance(ro)));
        Assert.assertThat(ro2, instanceOf(ReadOnlyByteBuf.class));
        Assert.assertThat(ro2.unwrap(), Matchers.sameInstance(buf));
        Assert.assertThat(ro2.readerIndex(), is(ro.readerIndex()));
        Assert.assertThat(ro2.writerIndex(), is(ro.writerIndex()));
        Assert.assertThat(ro2.capacity(), is(ro.capacity()));
        Assert.assertThat(ro2.maxCapacity(), is(ro.maxCapacity()));
    }

    @Test
    public void testReadOnlyOfDuplicate() throws Exception {
        ByteBuf buf = Unpooled.buffer(8).setIndex(1, 7);
        ByteBuf dup = buf.duplicate().setIndex(2, 6);
        ByteBuf ro = Unpooled.unmodifiableBuffer(dup);
        Assert.assertThat(ro, instanceOf(ReadOnlyByteBuf.class));
        Assert.assertThat(ro.unwrap(), Matchers.sameInstance(buf));
        Assert.assertThat(ro.readerIndex(), is(dup.readerIndex()));
        Assert.assertThat(ro.writerIndex(), is(dup.writerIndex()));
        Assert.assertThat(ro.capacity(), is(dup.capacity()));
        Assert.assertThat(ro.maxCapacity(), is(dup.maxCapacity()));
    }

    @Test
    public void testDuplicateOfReadOnly() throws Exception {
        ByteBuf buf = Unpooled.buffer(8).setIndex(1, 7);
        ByteBuf ro = Unpooled.unmodifiableBuffer(buf).setIndex(2, 6);
        ByteBuf dup = ro.duplicate();
        Assert.assertThat(dup, instanceOf(ReadOnlyByteBuf.class));
        Assert.assertThat(dup.unwrap(), Matchers.sameInstance(buf));
        Assert.assertThat(dup.readerIndex(), is(ro.readerIndex()));
        Assert.assertThat(dup.writerIndex(), is(ro.writerIndex()));
        Assert.assertThat(dup.capacity(), is(ro.capacity()));
        Assert.assertThat(dup.maxCapacity(), is(ro.maxCapacity()));
    }

    @Test
    public void testSwap() throws Exception {
        ByteBuf buf = Unpooled.buffer(8).setIndex(1, 7);
        ByteBuf swapped = buf.order(ByteOrder.LITTLE_ENDIAN);
        Assert.assertThat(swapped, instanceOf(SwappedByteBuf.class));
        Assert.assertThat(swapped.unwrap(), Matchers.sameInstance(buf));
        Assert.assertThat(swapped.order(ByteOrder.LITTLE_ENDIAN), Matchers.sameInstance(swapped));
        Assert.assertThat(swapped.order(ByteOrder.BIG_ENDIAN), Matchers.sameInstance(buf));
        buf.setIndex(2, 6);
        Assert.assertThat(swapped.readerIndex(), is(2));
        Assert.assertThat(swapped.writerIndex(), is(6));
    }

    @Test
    public void testMixture() throws Exception {
        ByteBuf buf = Unpooled.buffer(10000);
        ByteBuf derived = buf;
        Random rnd = new Random();
        for (int i = 0; i < (buf.capacity()); i++) {
            ByteBuf newDerived;
            switch (rnd.nextInt(4)) {
                case 0 :
                    newDerived = derived.slice(1, ((derived.capacity()) - 1));
                    break;
                case 1 :
                    newDerived = derived.duplicate();
                    break;
                case 2 :
                    newDerived = derived.order(((derived.order()) == (ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN));
                    break;
                case 3 :
                    newDerived = Unpooled.unmodifiableBuffer(derived);
                    break;
                default :
                    throw new Error();
            }
            Assert.assertThat(("nest level of " + newDerived), ByteBufDerivationTest.nestLevel(newDerived), is(lessThanOrEqualTo(3)));
            Assert.assertThat(("nest level of " + (newDerived.order(ByteOrder.BIG_ENDIAN))), ByteBufDerivationTest.nestLevel(newDerived.order(ByteOrder.BIG_ENDIAN)), is(lessThanOrEqualTo(2)));
            derived = newDerived;
        }
    }
}

