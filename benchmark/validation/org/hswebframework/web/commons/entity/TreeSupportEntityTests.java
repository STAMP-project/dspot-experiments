package org.hswebframework.web.commons.entity;


import SerializerFeature.PrettyFormat;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;


public class TreeSupportEntityTests {
    @Test
    public void test() {
        MenuEntity parent = builder().build();
        setName("menu-1");
        setId(1);
        setParentId((-1));
        MenuEntity m101 = builder().build();
        setName("menu-101");
        setId(101);
        setParentId(1);
        MenuEntity m102 = builder().build();
        setName("menu-102");
        setId(102);
        setParentId(1);
        MenuEntity m10201 = builder().build();
        setName("menu-10201");
        setId(10201);
        setParentId(102);
        // list??????
        List<MenuEntity> tree = TreeSupportEntity.list2tree(Arrays.asList(parent, m101, m102, m10201), MenuEntity::setChildren, ((Predicate<MenuEntity>) (( menu) -> getParentId().equals((-1)))));
        Assert.assertEquals(getChildren().get(0).getId(), Integer.valueOf(101));
        Assert.assertEquals(getChildren().get(1).getId(), Integer.valueOf(102));
        Assert.assertEquals(getChildren().get(0).getId(), Integer.valueOf(10201));
        System.out.println(JSON.toJSONString(tree, PrettyFormat));
        List<MenuEntity> list = new ArrayList<>();
        // ????????list
        TreeSupportEntity.expandTree2List(tree.get(0), list, () -> ((int) (Math.round(((Math.random()) * 1000000)))), MenuEntity::setChildren);
        System.out.println(JSON.toJSONString(list, PrettyFormat));
        Assert.assertEquals(list.size(), 4);
    }
}

