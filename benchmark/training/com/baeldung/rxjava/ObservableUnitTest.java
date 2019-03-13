package com.baeldung.rxjava;


import junit.framework.Assert;
import org.junit.Test;
import rx.Observable;


public class ObservableUnitTest {
    private String result = "";

    @Test
    public void givenString_whenJustAndSubscribe_thenEmitsSingleItem() {
        Observable<String> observable = Observable.just("Hello");
        observable.subscribe(( s) -> result = s);
        Assert.assertTrue(result.equals("Hello"));
    }

    @Test
    public void givenArray_whenFromAndSubscribe_thenEmitsItems() {
        String[] letters = new String[]{ "a", "b", "c", "d", "e", "f", "g" };
        Observable<String> observable = Observable.from(letters);
        observable.subscribe(( i) -> result += i, Throwable::printStackTrace, () -> result += "_Complete");
        Assert.assertTrue(result.equals("abcdefg_Complete"));
    }

    @Test
    public void givenArray_whenConvertsObservabletoBlockingObservable_thenReturnFirstElement() {
        String[] letters = new String[]{ "a", "b", "c", "d", "e", "f", "g" };
        Observable<String> observable = Observable.from(letters);
        String blockingObservable = observable.toBlocking().first();
        observable.subscribe(( i) -> result += i, Throwable::printStackTrace, () -> result += "_Completed");
        Assert.assertTrue(String.valueOf(result.charAt(0)).equals(blockingObservable));
    }

    @Test
    public void givenArray_whenMapAndSubscribe_thenReturnCapitalLetters() {
        String[] letters = new String[]{ "a", "b", "c", "d", "e", "f", "g" };
        Observable.from(letters).map(String::toUpperCase).subscribe(( letter) -> result += letter);
        Assert.assertTrue(result.equals("ABCDEFG"));
    }

    @Test
    public void givenArray_whenFlatMapAndSubscribe_thenReturnUpperAndLowerCaseLetters() {
        Observable.just("book1", "book2").flatMap(( s) -> getTitle()).subscribe(( l) -> result += l);
        Assert.assertTrue(result.equals("titletitle"));
    }

    @Test
    public void givenArray_whenScanAndSubscribe_thenReturnTheSumOfAllLetters() {
        String[] letters = new String[]{ "a", "b", "c" };
        Observable.from(letters).scan(new StringBuilder(), StringBuilder::append).subscribe(( total) -> result += total.toString());
        Assert.assertTrue(result.equals("aababc"));
    }

    @Test
    public void givenArrayOfNumbers_whenGroupBy_thenCreateTwoGroupsBasedOnParity() {
        Integer[] numbers = new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        String[] EVEN = new String[]{ "" };
        String[] ODD = new String[]{ "" };
        Observable.from(numbers).groupBy(( i) -> 0 == (i % 2) ? "EVEN" : "ODD").subscribe(( group) -> group.subscribe(( number) -> {
            if (group.getKey().equals("EVEN")) {
                EVEN[0] += number;
            } else {
                ODD[0] += number;
            }
        }));
        Assert.assertTrue(EVEN[0].equals("0246810"));
        Assert.assertTrue(ODD[0].equals("13579"));
    }

    @Test
    public void givenArrayOfNumbers_whenFilter_thenGetAllOddNumbers() {
        Integer[] numbers = new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Observable.from(numbers).filter(( i) -> (i % 2) == 1).subscribe(( i) -> result += i);
        Assert.assertTrue(result.equals("13579"));
    }

    @Test
    public void givenEmptyObservable_whenDefaultIfEmpty_thenGetDefaultMessage() {
        Observable.empty().defaultIfEmpty("Observable is empty").subscribe(( s) -> result += s);
        Assert.assertTrue(result.equals("Observable is empty"));
    }

    @Test
    public void givenObservableFromArray_whenDefaultIfEmptyAndFirst_thenGetFirstLetterFromArray() {
        String[] letters = new String[]{ "a", "b", "c", "d", "e", "f", "g" };
        Observable.from(letters).defaultIfEmpty("Observable is empty").first().subscribe(( s) -> result += s);
        Assert.assertTrue(result.equals("a"));
    }

    @Test
    public void givenObservableFromArray_whenTakeWhile_thenGetSumOfNumbersFromCondition() {
        Integer[] numbers = new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final Integer[] sum = new Integer[]{ 0 };
        Observable.from(numbers).takeWhile(( i) -> i < 5).subscribe(( s) -> sum[0] += s);
        Assert.assertTrue(((sum[0]) == 10));
    }
}

