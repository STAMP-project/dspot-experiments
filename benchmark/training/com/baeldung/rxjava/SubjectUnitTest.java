package com.baeldung.rxjava;


import junit.framework.Assert;
import org.junit.Test;
import rx.subjects.PublishSubject;

import static SubjectImpl.subscriber1;
import static SubjectImpl.subscriber2;


public class SubjectUnitTest {
    @Test
    public void givenSubjectAndTwoSubscribers_whenSubscribeOnSubject_thenSubscriberBeginsToAdd() {
        PublishSubject<Integer> subject = PublishSubject.create();
        subject.subscribe(SubjectImpl.getFirstObserver());
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);
        subject.subscribe(SubjectImpl.getSecondObserver());
        subject.onNext(4);
        subject.onCompleted();
        Assert.assertTrue((((subscriber1) + (subscriber2)) == 14));
    }
}

