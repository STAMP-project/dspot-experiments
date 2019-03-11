package com.blade.ioc;


import com.blade.ioc.bean.ClassInfo;
import com.blade.ioc.bean.Scanner;
import com.blade.ioc.reader.JarReaderImpl;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class JarReaderTest {
    @Test
    public void testJarReader() {
        JarReaderImpl jarReader = new JarReaderImpl();
        Set<ClassInfo> classInfos = jarReader.readClasses(Scanner.builder().packageName("org.slf4j.impl").build());
        Assert.assertNotNull(classInfos);
    }
}

