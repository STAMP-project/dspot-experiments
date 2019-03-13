package com.shekhargulati.random;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class RandomProblem004Test {
    @Test
    public void sumOfFirst10NumbersIs55() throws Exception {
        int sumOfNNumbers = RandomProblem004.sumOfNNumbers(10);
        Assert.assertThat(sumOfNNumbers, CoreMatchers.equalTo(55));
    }

    @Test
    public void squareOf10Is100() throws Exception {
        int square = RandomProblem004.squareOfANumber(10);
        Assert.assertThat(square, CoreMatchers.equalTo(100));
    }

    @Test
    public void sumOfFirst10NumbersIs55_1() throws Exception {
        int sum = RandomProblem004.sumOfNNumbers1(10);
        Assert.assertThat(sum, CoreMatchers.equalTo(55));
    }
}

