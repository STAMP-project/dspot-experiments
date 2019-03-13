package com.baeldung.list.listoflist;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ListOfListsUnitTest {
    private List<List<? extends Stationery>> listOfLists = new ArrayList<>();

    private List<Pen> penList = new ArrayList<>();

    private List<Pencil> pencilList = new ArrayList<>();

    private List<Rubber> rubberList = new ArrayList<>();

    @Test
    public void givenListOfLists_thenCheckNames() {
        Assert.assertEquals("Pen 1", getName());
        Assert.assertEquals("Pencil 1", getName());
        Assert.assertEquals("Rubber 1", getName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void givenListOfLists_whenRemovingElements_thenCheckNames() {
        ((ArrayList<Pencil>) (listOfLists.get(1))).remove(0);
        listOfLists.remove(1);
        Assert.assertEquals("Rubber 1", getName());
        listOfLists.remove(0);
        Assert.assertEquals("Rubber 1", getName());
    }

    @Test
    public void givenThreeList_whenCombineIntoOneList_thenCheckList() {
        ArrayList<Pen> pens = new ArrayList<>();
        pens.add(new Pen("Pen 1"));
        pens.add(new Pen("Pen 2"));
        ArrayList<Pencil> pencils = new ArrayList<>();
        pencils.add(new Pencil("Pencil 1"));
        pencils.add(new Pencil("Pencil 2"));
        ArrayList<Rubber> rubbers = new ArrayList<>();
        rubbers.add(new Rubber("Rubber 1"));
        rubbers.add(new Rubber("Rubber 2"));
        List<ArrayList<? extends Stationery>> list = new ArrayList<ArrayList<? extends Stationery>>();
        list.add(pens);
        list.add(pencils);
        list.add(rubbers);
        Assert.assertEquals("Pen 1", getName());
        Assert.assertEquals("Pencil 1", getName());
        Assert.assertEquals("Rubber 1", getName());
    }
}

