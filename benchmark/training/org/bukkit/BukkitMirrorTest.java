package org.bukkit;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class BukkitMirrorTest {
    @Parameterized.Parameter(0)
    public Method server;

    @Parameterized.Parameter(1)
    public String name;

    private Method bukkit;

    @Test
    public void isStatic() throws Throwable {
        Assert.assertThat(Modifier.isStatic(bukkit.getModifiers()), is(true));
    }

    @Test
    public void isDeprecated() throws Throwable {
        Assert.assertThat(bukkit.isAnnotationPresent(Deprecated.class), is(server.isAnnotationPresent(Deprecated.class)));
    }

    @Test
    public void returnType() throws Throwable {
        Assert.assertThat(bukkit.getReturnType(), is(((Object) (server.getReturnType()))));
        Assert.assertThat(bukkit.getGenericReturnType(), is(server.getGenericReturnType()));
    }

    @Test
    public void parameterTypes() throws Throwable {
        Assert.assertThat(bukkit.getGenericParameterTypes(), is(server.getGenericParameterTypes()));
    }

    @Test
    public void declaredException() throws Throwable {
        Assert.assertThat(bukkit.getGenericExceptionTypes(), is(server.getGenericExceptionTypes()));
    }
}

