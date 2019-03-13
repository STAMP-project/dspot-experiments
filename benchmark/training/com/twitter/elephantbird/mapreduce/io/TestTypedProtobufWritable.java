package com.twitter.elephantbird.mapreduce.io;


import Environment.Variable;
import com.google.protobuf.Message;
import com.twitter.data.proto.tutorial.AddressBookProtos.AddressBook;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment;
import org.junit.Assert;
import org.junit.Test;


public class TestTypedProtobufWritable {
    static AddressBook referenceAb;

    static TypedProtobufWritable<AddressBook> referenceAbWritable;

    @Test
    public void testReadWrite() throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("test.txt"));
        TestTypedProtobufWritable.referenceAbWritable.write(dos);
        dos.close();
        DataInputStream dis = new DataInputStream(new FileInputStream("test.txt"));
        TypedProtobufWritable<AddressBook> after = new TypedProtobufWritable<AddressBook>();
        after.readFields(dis);
        dis.close();
        AddressBook ab2 = after.get();
        Assert.assertEquals(TestTypedProtobufWritable.referenceAb, ab2);
        Assert.assertEquals(TestTypedProtobufWritable.referenceAbWritable.hashCode(), after.hashCode());
    }

    @Test
    public void testMessageReadWrite() throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("test2.txt"));
        TestTypedProtobufWritable.referenceAbWritable.write(dos);
        dos.close();
        DataInputStream dis = new DataInputStream(new FileInputStream("test2.txt"));
        TypedProtobufWritable<Message> after = new TypedProtobufWritable<Message>();
        after.readFields(dis);
        dis.close();
        AddressBook ab2 = ((AddressBook) (after.get()));
        Assert.assertEquals(TestTypedProtobufWritable.referenceAb, ab2);
        Assert.assertEquals(TestTypedProtobufWritable.referenceAbWritable.hashCode(), after.hashCode());
    }

    @Test
    public void testMessageReadWriteEmpty() throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("test3.txt"));
        TypedProtobufWritable<AddressBook> empty = new TypedProtobufWritable<AddressBook>();
        empty.write(dos);
        dos.close();
        DataInputStream dis = new DataInputStream(new FileInputStream("test3.txt"));
        TypedProtobufWritable<Message> after = new TypedProtobufWritable<Message>();
        after.readFields(dis);
        dis.close();
        AddressBook ab2 = ((AddressBook) (after.get()));
        Assert.assertNull(ab2);
    }

    @Test
    public void testStableHashcodeAcrossJVMs() throws IOException {
        int expectedHashCode = TestTypedProtobufWritable.referenceAbWritable.hashCode();
        Java otherJvm = new Java();
        otherJvm.setNewenvironment(true);
        otherJvm.setFork(true);
        otherJvm.setProject(new Project());
        otherJvm.setClassname(TestTypedProtobufWritable.OtherJvmClass.class.getName());
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            Environment.Variable var = new Environment.Variable();
            var.setKey(entry.getKey());
            var.setValue(entry.getValue());
            otherJvm.addEnv(var);
        }
        for (String prop : System.getProperties().stringPropertyNames()) {
            String propValue = System.getProperty(prop);
            Environment.Variable var = new Environment.Variable();
            var.setKey(prop);
            var.setValue(propValue);
            otherJvm.addSysproperty(var);
        }
        otherJvm.setDir(new File(System.getProperty("java.io.tmpdir")));
        File tmpOut = File.createTempFile("otherJvm", "txt");
        otherJvm.setArgs(tmpOut.getAbsolutePath());
        otherJvm.init();
        otherJvm.executeJava();
        DataInputStream is = new DataInputStream(new FileInputStream(tmpOut));
        Assert.assertEquals(expectedHashCode, is.readInt());
        is.close();
    }

    public static class OtherJvmClass {
        /* Used for testStableHashcodeAcrossJVMs */
        public static void main(String[] args) throws IOException {
            TestTypedProtobufWritable.setUp();
            int hashCode = TestTypedProtobufWritable.referenceAbWritable.hashCode();
            File tmpFile = new File(args[0]);
            DataOutputStream os = new DataOutputStream(new FileOutputStream(tmpFile));
            os.writeInt(hashCode);
            os.close();
            System.exit(0);
        }
    }
}

