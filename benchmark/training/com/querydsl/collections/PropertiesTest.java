package com.querydsl.collections;


import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static QStateHistory.stateHistory;
import static QStateHistoryOwner.stateHistoryOwner;


public class PropertiesTest {
    @Test
    public void hidden() {
        QStateHistory history = stateHistory;
        List<StateHistory> histories = Collections.singletonList(new StateHistory());
        Assert.assertEquals(1, CollQueryFactory.from(history, histories).where(history.changedAt.isNull()).fetch().size());
    }

    @Test
    public void hidden2() {
        QStateHistoryOwner historyOwner = stateHistoryOwner;
        List<StateHistoryOwner> historyOwners = Collections.singletonList(new StateHistoryOwner());
        Assert.assertEquals(1, CollQueryFactory.from(historyOwner, historyOwners).where(historyOwner.stateHistory.isNull()).fetch().size());
    }
}

