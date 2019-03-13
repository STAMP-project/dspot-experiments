/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.plugin.service;


import com.sishuok.es.common.plugin.entity.Tree;
import com.sishuok.es.common.test.BaseIT;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-2-9 ??3:52
 * <p>Version: 1.0
 */
public class TreeServiceIT extends BaseIT {
    @Autowired
    private TreeService treeService;

    private Tree root = null;

    @Test
    public void testMoveAsChildWithNoChild() {
        Tree source = createTree(getId(), root.makeSelfAsNewParentIds());
        Tree target = createTree(getId(), root.makeSelfAsNewParentIds());
        flush();
        move(source, target, "inner");
        clear();
        Assert.assertEquals(getId(), treeService.findOne(getId()).getParentId());
        Assert.assertEquals(target.makeSelfAsNewParentIds(), treeService.findOne(getId()).getParentIds());
        Assert.assertEquals(Integer.valueOf(1), treeService.findOne(getId()).getWeight());
    }

    @Test
    public void testMoveAsChildWithChild() {
        Tree source = createTree(getId(), root.makeSelfAsNewParentIds());
        Tree target = createTree(getId(), root.makeSelfAsNewParentIds());
        Tree t = createTree(getId(), target.getParentIds());
        move(source, target, "inner");
        clear();
        Assert.assertEquals(getId(), treeService.findOne(getId()).getParentId());
        Assert.assertEquals(target.makeSelfAsNewParentIds(), treeService.findOne(getId()).getParentIds());
        Assert.assertEquals(Integer.valueOf(2), treeService.findOne(getId()).getWeight());
    }

    @Test
    public void testMoveAsBeforeWithNoPrevSiblings() {
        Tree target = createTree(getId(), root.makeSelfAsNewParentIds());
        int oldTargetWeight = target.getWeight();
        Tree t = createTree(getId(), target.makeSelfAsNewParentIds());
        Tree source = createTree(getId(), root.makeSelfAsNewParentIds());
        int oldSourceWeight = source.getWeight();
        move(source, target, "prev");
        clear();
        Assert.assertEquals(target.getParentId(), treeService.findOne(getId()).getParentId());
        Assert.assertEquals(target.getParentIds(), treeService.findOne(getId()).getParentIds());
        Assert.assertEquals(Integer.valueOf(oldTargetWeight), treeService.findOne(getId()).getWeight());
        Assert.assertEquals(Integer.valueOf(oldSourceWeight), treeService.findOne(getId()).getWeight());
    }

    @Test
    public void testMoveAsBeforeWithPrevSiblings() {
        Tree t = createTree(getId(), root.makeSelfAsNewParentIds());
        Tree target = createTree(getId(), root.makeSelfAsNewParentIds());
        int oldTargetWeight = target.getWeight();
        Tree source = createTree(getId(), root.makeSelfAsNewParentIds());
        int oldSourceWeight = source.getWeight();
        move(source, target, "prev");
        clear();
        Assert.assertEquals(target.getParentId(), treeService.findOne(getId()).getParentId());
        Assert.assertEquals(target.getParentIds(), treeService.findOne(getId()).getParentIds());
        Assert.assertEquals(Integer.valueOf(oldTargetWeight), treeService.findOne(getId()).getWeight());
        Assert.assertEquals(Integer.valueOf(oldSourceWeight), treeService.findOne(getId()).getWeight());
    }

    @Test
    public void testMoveAsBeforeWithPrevSiblings2() {
        Tree t11 = createTree(getId(), root.makeSelfAsNewParentIds());
        Tree target = createTree(getId(), root.makeSelfAsNewParentIds());
        int oldTargetWeight = target.getWeight();
        Tree t12 = createTree(getId(), root.makeSelfAsNewParentIds());
        int oldT12Weight = t12.getWeight();
        Tree source = createTree(getId(), root.makeSelfAsNewParentIds());
        int oldSourceWeight = source.getWeight();
        Tree t13 = createTree(getId(), root.makeSelfAsNewParentIds());
        move(source, target, "prev");
        clear();
        Assert.assertEquals(target.getParentId(), treeService.findOne(getId()).getParentId());
        Assert.assertEquals(target.getParentId(), treeService.findOne(getId()).getParentId());
        Assert.assertEquals(target.getParentIds(), treeService.findOne(getId()).getParentIds());
        Assert.assertEquals(Integer.valueOf(oldTargetWeight), treeService.findOne(getId()).getWeight());
        Assert.assertEquals(Integer.valueOf(oldSourceWeight), treeService.findOne(getId()).getWeight());
        Assert.assertEquals(Integer.valueOf(oldT12Weight), treeService.findOne(getId()).getWeight());
    }

    @Test
    public void testMoveAsBeforeWithPrevSiblingsAndContains() {
        Tree newRoot = createTree(getId(), root.makeSelfAsNewParentIds());
        Tree t11 = createTree(getId(), newRoot.makeSelfAsNewParentIds());
        Tree target = createTree(getId(), newRoot.makeSelfAsNewParentIds());
        int oldTargetWeight = target.getWeight();
        Tree t12 = createTree(getId(), newRoot.makeSelfAsNewParentIds());
        int oldT12Weight = t12.getWeight();
        Tree source = createTree(getId(), root.makeSelfAsNewParentIds());
        Tree sourceChild = createTree(getId(), source.makeSelfAsNewParentIds());
        move(source, target, "prev");
        clear();
        Assert.assertEquals(target.getParentId(), treeService.findOne(getId()).getParentId());
        Assert.assertEquals(target.getParentIds(), treeService.findOne(getId()).getParentIds());
        Assert.assertEquals(getId(), treeService.findOne(getId()).getParentId());
        Assert.assertEquals(treeService.findOne(getId()).makeSelfAsNewParentIds(), treeService.findOne(getId()).getParentIds());
        Assert.assertEquals(Integer.valueOf(oldTargetWeight), treeService.findOne(getId()).getWeight());
        Assert.assertEquals(Integer.valueOf(oldT12Weight), treeService.findOne(getId()).getWeight());
        Assert.assertEquals(Integer.valueOf((oldT12Weight + 1)), treeService.findOne(getId()).getWeight());
    }

    @Test
    public void testMoveAsAfterWithNoAfterSiblings() {
        Tree newRoot = createTree(getId(), root.makeSelfAsNewParentIds());
        Tree t = createTree(getId(), newRoot.makeSelfAsNewParentIds());
        Tree target = createTree(getId(), newRoot.makeSelfAsNewParentIds());
        int oldTargetWeight = target.getWeight();
        Tree source = createTree(getId(), newRoot.makeSelfAsNewParentIds());
        move(source, target, "next");
        clear();
        Assert.assertEquals(Integer.valueOf((oldTargetWeight + 1)), treeService.findOne(getId()).getWeight());
        Assert.assertEquals(Integer.valueOf(oldTargetWeight), treeService.findOne(getId()).getWeight());
    }

    @Test
    public void testMoveAsAfterWithAfterSiblings() {
        Tree newRoot = createTree(getId(), root.makeSelfAsNewParentIds());
        Tree target = createTree(getId(), newRoot.makeSelfAsNewParentIds());
        Tree t12 = createTree(getId(), newRoot.makeSelfAsNewParentIds());
        int oldT12Weight = t12.getWeight();
        Tree source = createTree(getId(), newRoot.makeSelfAsNewParentIds());
        move(source, target, "next");
        clear();
        Assert.assertEquals(target.getParentId(), treeService.findOne(getId()).getParentId());
        Assert.assertEquals(target.getParentIds(), treeService.findOne(getId()).getParentIds());
        Assert.assertEquals(Integer.valueOf(oldT12Weight), treeService.findOne(getId()).getWeight());
        Assert.assertEquals(Integer.valueOf((oldT12Weight + 1)), treeService.findOne(getId()).getWeight());
    }
}

