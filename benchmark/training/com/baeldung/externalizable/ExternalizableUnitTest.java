package com.baeldung.externalizable;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class ExternalizableUnitTest {
    private static final String OUTPUT_FILE = "externalizable.txt";

    @Test
    public void whenSerializing_thenUseExternalizable() throws IOException, ClassNotFoundException {
        Country c = new Country();
        c.setCapital("Yerevan");
        c.setCode(374);
        c.setName("Armenia");
        FileOutputStream fileOutputStream = new FileOutputStream(ExternalizableUnitTest.OUTPUT_FILE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        c.writeExternal(objectOutputStream);
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStream.close();
        FileInputStream fileInputStream = new FileInputStream(ExternalizableUnitTest.OUTPUT_FILE);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Country c2 = new Country();
        c2.readExternal(objectInputStream);
        objectInputStream.close();
        fileInputStream.close();
        Assert.assertTrue(((c2.getCode()) == (c.getCode())));
        Assert.assertTrue(c2.getName().equals(c.getName()));
    }

    @Test
    public void whenInheritanceSerialization_then_UseExternalizable() throws IOException, ClassNotFoundException {
        Region r = new Region();
        r.setCapital("Yerevan");
        r.setCode(374);
        r.setName("Armenia");
        r.setClimate("Mediterranean");
        r.setPopulation(120.0);
        FileOutputStream fileOutputStream = new FileOutputStream(ExternalizableUnitTest.OUTPUT_FILE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        r.writeExternal(objectOutputStream);
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStream.close();
        FileInputStream fileInputStream = new FileInputStream(ExternalizableUnitTest.OUTPUT_FILE);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Region r2 = new Region();
        r2.readExternal(objectInputStream);
        objectInputStream.close();
        fileInputStream.close();
        Assert.assertTrue(((r2.getPopulation()) == null));
    }
}

