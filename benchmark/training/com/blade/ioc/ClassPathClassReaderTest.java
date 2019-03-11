package com.blade.ioc;


import com.blade.ioc.bean.ClassInfo;
import com.blade.ioc.reader.ClassPathClassReader;
import com.blade.mvc.annotation.Path;
import com.blade.types.controller.IndexController;
import com.blade.types.controller.UserService;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class ClassPathClassReaderTest {
    @Test
    public void testClassPathReader() {
        String packageName = "com.blade.types.controller";
        ClassReader classReader = DynamicContext.getClassReader(packageName);
        Assert.assertEquals(ClassPathClassReader.class, classReader.getClass());
        Set<ClassInfo> classInfos = classReader.readClasses(com.blade.ioc.bean.Scanner.builder().packageName(packageName).build());
        Assert.assertEquals(2, classInfos.size());
        classInfos = classReader.readClasses(com.blade.ioc.bean.Scanner.builder().packageName(packageName).parent(Runnable.class).build());
        Assert.assertEquals(1, classInfos.size());
        Assert.assertEquals(UserService.class, classInfos.stream().findFirst().get().getClazz());
        classInfos = classReader.readClasses(com.blade.ioc.bean.Scanner.builder().packageName(packageName).annotation(Path.class).build());
        Assert.assertEquals(1, classInfos.size());
        Assert.assertEquals(IndexController.class, classInfos.stream().findFirst().get().getClazz());
        classInfos = classReader.readClasses(com.blade.ioc.bean.Scanner.builder().packageName(packageName).parent(Object.class).annotation(Path.class).build());
        Assert.assertEquals(1, classInfos.size());
        Assert.assertEquals(IndexController.class, classInfos.stream().findFirst().get().getClazz());
    }
}

