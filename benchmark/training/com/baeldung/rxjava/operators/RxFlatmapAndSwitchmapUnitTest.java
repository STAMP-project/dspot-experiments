package com.baeldung.rxjava.operators;


import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class RxFlatmapAndSwitchmapUnitTest {
    @Test
    public void givenObservable_whenFlatmap_shouldAssertAllItemsReturned() {
        // given
        List<String> actualOutput = new ArrayList<>();
        final TestScheduler scheduler = new TestScheduler();
        final List<String> keywordToSearch = Arrays.asList("b", "bo", "boo", "book", "books");
        // when
        Observable.fromIterable(keywordToSearch).flatMap(( s) -> Observable.just((s + " FirstResult"), (s + " SecondResult")).delay(10, TimeUnit.SECONDS, scheduler)).toList().doOnSuccess(( s) -> actualOutput.addAll(s)).subscribe();
        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
        // then
        Assert.assertThat(actualOutput, CoreMatchers.hasItems("b FirstResult", "b SecondResult", "boo FirstResult", "boo SecondResult", "bo FirstResult", "bo SecondResult", "book FirstResult", "book SecondResult", "books FirstResult", "books SecondResult"));
    }

    @Test
    public void givenObservable_whenSwitchmap_shouldAssertLatestItemReturned() {
        // given
        List<String> actualOutput = new ArrayList<>();
        final TestScheduler scheduler = new TestScheduler();
        final List<String> keywordToSearch = Arrays.asList("b", "bo", "boo", "book", "books");
        // when
        Observable.fromIterable(keywordToSearch).switchMap(( s) -> Observable.just((s + " FirstResult"), (s + " SecondResult")).delay(10, TimeUnit.SECONDS, scheduler)).toList().doOnSuccess(( s) -> actualOutput.addAll(s)).subscribe();
        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
        // then
        Assert.assertEquals(2, actualOutput.size());
        Assert.assertThat(actualOutput, CoreMatchers.hasItems("books FirstResult", "books SecondResult"));
    }
}

