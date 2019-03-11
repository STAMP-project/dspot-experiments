package org.embulk.config;


import org.embulk.EmbulkTestRuntime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestTaskSource {
    private static interface TypeFields extends Task {
        public boolean getBoolean();

        public void setBoolean(boolean v);

        public double getDouble();

        public void setDouble(double v);

        public int getInt();

        public void setInt(int v);

        public long getLong();

        public void setLong(long v);

        public String getString();

        public void setString(String v);
    }

    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    private TaskSource taskSource;

    @Test
    public void testEqualsOfLoadedTasks() {
        TestTaskSource.TypeFields task = taskSource.loadTask(TestTaskSource.TypeFields.class);
        task.setBoolean(true);
        task.setDouble(0.2);
        task.setInt(3);
        task.setLong(Long.MAX_VALUE);
        task.setString("sf");
        TaskSource taskSource2 = dump();
        TestTaskSource.TypeFields task2 = taskSource2.loadTask(TestTaskSource.TypeFields.class);
        Assert.assertTrue(task.equals(task2));
        Assert.assertTrue(((task.hashCode()) == (task2.hashCode())));
        task.setBoolean(false);
        Assert.assertFalse(task.equals(task2));
        Assert.assertFalse(((task.hashCode()) == (task2.hashCode())));
    }
}

