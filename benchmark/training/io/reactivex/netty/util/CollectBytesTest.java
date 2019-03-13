package io.reactivex.netty.util;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.reactivex.netty.util.CollectBytes.TooMuchDataException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public class CollectBytesTest {
    @Test
    public void testCollectOverEmptyObservable() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        Observable.<ByteBuf>empty().compose(CollectBytes.all()).subscribe(t);
        t.assertNoErrors();
        t.assertCompleted();
        t.assertValue(Unpooled.buffer());
    }

    @Test
    public void testCollectSingleEvent() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        Observable.just(getByteBuf("test")).compose(CollectBytes.all()).subscribe(t);
        t.assertNoErrors();
        t.assertCompleted();
        t.assertValues(getByteBuf("test"));
    }

    @Test
    public void testCollectManyEvents() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        Observable.just(getByteBuf("t"), getByteBuf("e"), getByteBuf("s"), getByteBuf("t")).compose(CollectBytes.all()).subscribe(t);
        t.assertNoErrors();
        t.assertCompleted();
        t.assertValues(getByteBuf("test"));
    }

    @Test
    public void testWithLimitEqualToBytes() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        Observable.just(getByteBuf("t"), getByteBuf("e"), getByteBuf("s"), getByteBuf("t")).compose(CollectBytes.upTo(4)).subscribe(t);
        t.assertNoErrors();
        t.assertCompleted();
        t.assertValues(getByteBuf("test"));
    }

    @Test
    public void testWithLimitGreaterThanBytes() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        Observable.just(getByteBuf("t"), getByteBuf("e"), getByteBuf("s"), getByteBuf("t")).compose(CollectBytes.upTo(5)).subscribe(t);
        t.assertNoErrors();
        t.assertCompleted();
        t.assertValues(getByteBuf("test"));
    }

    @Test
    public void testCollectWithLimitSmallerThanBytes() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        Observable.just(getByteBuf("t"), getByteBuf("e"), getByteBuf("s"), getByteBuf("t")).compose(CollectBytes.upTo(2)).subscribe(t);
        t.assertError(TooMuchDataException.class);
        t.assertNotCompleted();
        t.assertNoValues();
    }

    @Test
    public void testReturnSingleEventWithMoreBytesThanMax() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        toByteBufObservable("test").compose(CollectBytes.upTo(0)).subscribe(t);
        t.assertError(TooMuchDataException.class);
        t.assertNotCompleted();
        t.assertNoValues();
    }

    @Test
    public void testReturnMultipleEvents() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        toByteBufObservable("1", "2").compose(CollectBytes.upTo(5)).subscribe(t);
        t.assertNoErrors();
        t.assertCompleted();
        t.assertValues(getByteBufs("12"));
    }

    @Test
    public void testReturnEventsOnLimitBoundary() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        toByteBufObservable("12", "34", "56").compose(CollectBytes.upTo(4)).subscribe(t);
        t.assertError(TooMuchDataException.class);
        t.assertNotCompleted();
        t.assertNoValues();
    }

    @Test
    public void testReturnMultipleEventsEndingWhenOverMaxBytes() throws Exception {
        TestSubscriber<ByteBuf> t = new TestSubscriber();
        toByteBufObservable("first", "second", "third").compose(CollectBytes.upTo(7)).subscribe(t);
        t.assertError(TooMuchDataException.class);
        t.assertNotCompleted();
        t.assertNoValues();
    }

    @Test
    public void testUnsubscribeFromUpstream() throws Exception {
        final List<String> emittedBufs = new ArrayList<>();
        toByteBufObservable("first", "second", "third").doOnNext(new rx.functions.Action1<ByteBuf>() {
            @Override
            public void call(ByteBuf byteBuf) {
                emittedBufs.add(byteBuf.toString(Charset.defaultCharset()));
            }
        }).compose(CollectBytes.upTo(7)).subscribe(new TestSubscriber());
        Assert.assertEquals(Arrays.asList("first", "second"), emittedBufs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionOnNegativeMaxBytes() throws Exception {
        CollectBytes.upTo((-1));
    }
}

