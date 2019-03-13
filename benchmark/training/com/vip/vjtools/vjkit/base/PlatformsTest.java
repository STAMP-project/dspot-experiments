package com.vip.vjtools.vjkit.base;


import org.junit.Test;

import static Platforms.IS_JAVA7;
import static Platforms.IS_JAVA8;
import static Platforms.IS_WINDOWS;
import static Platforms.JAVA_HOME;
import static Platforms.JAVA_SPECIFICATION_VERSION;
import static Platforms.JAVA_VERSION;
import static Platforms.OS_ARCH;
import static Platforms.OS_NAME;
import static Platforms.OS_VERSION;
import static Platforms.TMP_DIR;
import static Platforms.USER_HOME;
import static Platforms.WORKING_DIR;


public class PlatformsTest {
    @Test
    public void PlatformTest() {
        if (IS_WINDOWS) {
            assertThat(Platforms.FILE_PATH_SEPARATOR).isEqualTo("\\");
            assertThat(Platforms.FILE_PATH_SEPARATOR_CHAR).isEqualTo('\\');
        } else {
            assertThat(Platforms.FILE_PATH_SEPARATOR).isEqualTo("/");
            assertThat(Platforms.FILE_PATH_SEPARATOR_CHAR).isEqualTo('/');
        }
        System.out.println(("OS_NAME:" + (OS_NAME)));
        System.out.println(("OS_VERSION:" + (OS_VERSION)));
        System.out.println(("OS_ARCH:" + (OS_ARCH)));
        System.out.println(("JAVA_SPECIFICATION_VERSION:" + (JAVA_SPECIFICATION_VERSION)));
        System.out.println(("JAVA_VERSION:" + (JAVA_VERSION)));
        System.out.println(("JAVA_HOME:" + (JAVA_HOME)));
        System.out.println(("USER_HOME:" + (USER_HOME)));
        System.out.println(("TMP_DIR:" + (TMP_DIR)));
        System.out.println(("WORKING_DIR:" + (WORKING_DIR)));
        if (IS_JAVA7) {
            assertThat(Platforms.IS_ATLEASET_JAVA7).isTrue();
            assertThat(Platforms.IS_ATLEASET_JAVA8).isFalse();
        }
        if (IS_JAVA8) {
            assertThat(Platforms.IS_ATLEASET_JAVA7).isTrue();
            assertThat(Platforms.IS_ATLEASET_JAVA8).isTrue();
        }
    }
}

