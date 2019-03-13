/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.plugin.service;


import Sort.Direction;
import com.sishuok.es.common.plugin.entity.Move;
import com.sishuok.es.common.test.BaseIT;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-20 ??2:22
 * <p>Version: 1.0
 */
public class MoveServiceIT extends BaseIT {
    @Autowired
    private MoveService moveService;

    @Test
    public void testSave() {
        Move m1 = new Move();
        Move m2 = new Move();
        save(m1);
        save(m2);
        Assert.assertEquals(((Integer) ((m1.getWeight()) + (getStepLength()))), m2.getWeight());
    }

    // ??????to?????
    @Test
    public void testUpWithSerialAndToIsLast() {
        Move from = createMove();// 1000

        Move to = createMove();// 2000

        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        moveService.up(getId(), getId());
        from = moveService.findOne(getId());
        to = moveService.findOne(getId());
        Assert.assertEquals(toWeight, from.getWeight());
        Assert.assertEquals(fromWeight, to.getWeight());
    }

    // ?????????
    @Test
    public void testUpWithSerialAndToNotLast() {
        Move from = createMove();// 1000

        Move to = createMove();// 2000

        Move last = createMove();// 3000

        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        Integer lastWeight = last.getWeight();
        moveService.up(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(toWeight, from.getWeight());
        Assert.assertEquals(fromWeight, to.getWeight());
    }

    // ?????????
    @Test
    public void testUpWithNoSerialAndToIsLast() {
        Move from = createMove();// 1000

        Move middle1 = createMove();// 2000

        Move middle2 = createMove();// 3000

        Move to = createMove();// 4000

        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        moveService.up(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(toWeight, from.getWeight());
    }

    // ??????????
    @Test
    public void testUpWithNoSerialAndToNotLast() {
        Move from = createMove();// 1000

        Move middle1 = createMove();// 2000

        Move middle2 = createMove();// 3000

        Move to = createMove();// 4000

        Move last = createMove();// 5000

        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        Integer lastWeight = last.getWeight();
        moveService.up(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(toWeight, from.getWeight());
    }

    // ??????to????
    @Test
    public void testDownWithSerialAndToIsFirst() {
        Move to = createMove();// 1000

        Move from = createMove();// 2000

        Integer toWeight = to.getWeight();
        Integer fromWeight = from.getWeight();
        moveService.down(getId(), getId());
        from = moveService.findOne(getId());
        to = moveService.findOne(getId());
        Assert.assertEquals(toWeight, from.getWeight());
        Assert.assertEquals(fromWeight, to.getWeight());
    }

    // ????????
    @Test
    public void testDownWithSerialAndToNotFirst() {
        Move first = createMove();// 1000

        Move to = createMove();// 2000

        Move from = createMove();// 3000

        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        moveService.down(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(toWeight, from.getWeight());
        Assert.assertEquals(fromWeight, to.getWeight());
    }

    // ????????
    @Test
    public void testDownWithNoSerialAndToIsFirst() {
        Move to = createMove();// 1000

        Move middle1 = createMove();// 2000

        Move middle2 = createMove();// 3000

        Move from = createMove();// 4000

        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        moveService.down(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(toWeight, from.getWeight());
    }

    // ?????????
    @Test
    public void testDownWithNoSerialAndToNotFirst() {
        Move first = createMove();// 1000

        Move to = createMove();// 2000

        Move middle1 = createMove();// 3000

        Move middle2 = createMove();// 4000

        Move from = createMove();// 5000

        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        Integer firstWeight = first.getWeight();
        moveService.down(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(Integer.valueOf(toWeight), from.getWeight());
    }

    @Test(expected = IllegalStateException.class)
    public void testDownWithNotEnough() {
        Move first = createMove();// 1000

        Move to = createMove();// 2000

        for (int i = 0; i < 25; i++) {
            createMove();
        }
        Move from = createMove();
        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        Integer firstWeight = first.getWeight();
        first.setWeight((toWeight - 1));
        update(first);
        flush();
        moveService.down(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(toWeight, from.getWeight());
    }

    @Test
    public void testDownWithMiddle25AndNotFirst() {
        Move first = createMove();// 1000

        Move to = createMove();// 2000

        for (int i = 0; i < 25; i++) {
            createMove();
        }
        Move from = createMove();
        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        Integer firstWeight = first.getWeight();
        flush();
        moveService.down(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(Integer.valueOf((toWeight - ((toWeight - firstWeight) / 2))), from.getWeight());
    }

    @Test
    public void testDownWithMiddle25AndIsFirst() {
        Move to = createMove();// 1000

        for (int i = 0; i < 25; i++) {
            createMove();
        }
        Move from = createMove();
        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        flush();
        moveService.down(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(Integer.valueOf((toWeight / 2)), from.getWeight());
    }

    @Test
    public void testUpWithMiddle25AndNotLast() {
        Move from = createMove();// 2000

        for (int i = 0; i < 25; i++) {
            createMove();
        }
        Move to = createMove();
        Move last = createMove();
        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        Integer lastWeight = last.getWeight();
        flush();
        moveService.up(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(Integer.valueOf((toWeight + ((lastWeight - toWeight) / 2))), from.getWeight());
    }

    @Test
    public void testUpWithMiddle25AndIsLast() {
        Move from = createMove();
        for (int i = 0; i < 25; i++) {
            createMove();
        }
        Move to = createMove();
        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();
        flush();
        moveService.up(getId(), getId());
        from = moveService.findOne(getId());
        Assert.assertEquals(Integer.valueOf((toWeight + (getStepLength()))), from.getWeight());
    }

    @Test
    public void testReweight() {
        for (int i = 0; i < 20; i++) {
            Move move = createMove();
            move.setWeight(i);
        }
        flush();
        reweight();
        List<Move> moves = findAll(new org.springframework.data.domain.Sort(Direction.ASC, "weight"));
        Assert.assertEquals(getStepLength(), moves.get(0).getWeight());
        Assert.assertEquals(Integer.valueOf(((getStepLength()) * 2)), moves.get(1).getWeight());
        Assert.assertEquals(Integer.valueOf(((getStepLength()) * (moves.size()))), moves.get(((moves.size()) - 1)).getWeight());
    }
}

