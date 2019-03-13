package com.fernandocejas.frodo.internal;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CounterTest {
    private static final String COUNTER_NAME = "android10";

    private Counter counter;

    @Test
    public void incrementsCounter() {
        counter.increment();
        counter.increment();
        counter.increment();
        assertThat(counter.tally()).isEqualTo(3);
    }

    @Test
    public void clearsCounter() {
        counter.increment();
        counter.increment();
        assertThat(counter.tally()).isEqualTo(2);
        counter.clear();
        assertThat(counter.tally()).isZero();
    }

    @Test
    public void decrementsCounter() {
        counter.increment();
        counter.increment();
        counter.increment();
        counter.decrement();
        counter.decrement();
        assertThat(counter.tally()).isEqualTo(1);
    }

    @Test
    public void comparesCounterValues() {
        final Counter anotherCounter = new Counter("myCounter");
        counter.increment();
        counter.increment();
        assertThat(counter.compareTo(anotherCounter)).isEqualTo(1);
        anotherCounter.increment();
        anotherCounter.increment();
        assertThat(counter.compareTo(anotherCounter)).isZero();
        counter.decrement();
        assertThat(counter.compareTo(anotherCounter)).isEqualTo((-1));
    }

    @Test
    public void countsNegatives() {
        counter.decrement();
        counter.decrement();
        assertThat(counter.tally()).isEqualTo((-2));
    }
}

