package com.github.dockerjava.api;


import com.github.dockerjava.api.model.Binds;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.api.model.ResponseItem;
import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.reflect.FieldUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class ModelsSerializableTest {
    private static final Logger LOG = LoggerFactory.getLogger(ModelsSerializableTest.class);

    private List<String> excludeClasses = Arrays.asList(Binds.class.getName(), BuildResponseItem.class.getName(), PullResponseItem.class.getName(), PushResponseItem.class.getName(), ResponseItem.class.getName());

    @Test
    public void allModelsSerializable() throws IOException, IllegalAccessException, NoSuchFieldException {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        for (ClassPath.ClassInfo classInfo : ClassPath.from(contextClassLoader).getTopLevelClasses("com.github.dockerjava.api.model")) {
            if (classInfo.getName().endsWith("Test")) {
                continue;
            }
            final Class<?> aClass = classInfo.load();
            if ((aClass.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith("test-classes/")) || (aClass.isEnum())) {
                continue;
            }
            ModelsSerializableTest.LOG.debug("aClass: {}", aClass);
            Assert.assertThat(aClass, typeCompatibleWith(Serializable.class));
            final Object serialVersionUID = FieldUtils.readDeclaredStaticField(aClass, "serialVersionUID", true);
            if (!(excludeClasses.contains(aClass.getName()))) {
                Assert.assertThat(serialVersionUID, Matchers.instanceOf(Long.class));
                Assert.assertThat("Follow devel docs", ((Long) (serialVersionUID)), Matchers.is(1L));
            }
        }
    }
}

