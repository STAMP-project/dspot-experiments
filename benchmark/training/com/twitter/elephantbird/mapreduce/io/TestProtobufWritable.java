package com.twitter.elephantbird.mapreduce.io;


import Environment.Variable;
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


public class TestProtobufWritable {
    static AddressBook referenceAb;

    static ProtobufWritable<AddressBook> referenceAbWritable;

    @Test
    public void testReadWrite() throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("test.txt"));
        TestProtobufWritable.referenceAbWritable.write(dos);
        dos.close();
        DataInputStream dis = new DataInputStream(new FileInputStream("test.txt"));
        ProtobufWritable<AddressBook> after = new ProtobufWritable<AddressBook>(new com.twitter.elephantbird.util.TypeRef<AddressBook>() {});
        after.readFields(dis);
        dis.close();
        AddressBook ab2 = after.get();
        Assert.assertEquals(TestProtobufWritable.referenceAb, ab2);
        Assert.assertEquals(TestProtobufWritable.referenceAbWritable.hashCode(), after.hashCode());
    }

    @Test
    public void testStableHashcodeAcrossJVMs() throws IOException {
        int expectedHashCode = TestProtobufWritable.referenceAbWritable.hashCode();
        Java otherJvm = new Java();
        otherJvm.setNewenvironment(true);
        otherJvm.setFork(true);
        otherJvm.setProject(new Project());
        otherJvm.setClassname(TestProtobufWritable.OtherJvmClass.class.getName());
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
            TestProtobufWritable.setUp();
            int hashCode = TestProtobufWritable.referenceAbWritable.hashCode();
            File tmpFile = new File(args[0]);
            DataOutputStream os = new DataOutputStream(new FileOutputStream(tmpFile));
            os.writeInt(hashCode);
            os.close();
            System.exit(0);
        }
    }
}

