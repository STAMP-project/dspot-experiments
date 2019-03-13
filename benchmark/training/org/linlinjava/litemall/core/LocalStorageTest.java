package org.linlinjava.litemall.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.core.storage.LocalStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LocalStorageTest {
    @Autowired
    private LocalStorage localStorage;

    @Test
    public void test() throws IOException {
        String test = getClass().getClassLoader().getResource("litemall.png").getFile();
        File testFile = new File(test);
        localStorage.store(new FileInputStream(test), testFile.length(), "image/png", "litemall.png");
        Resource resource = localStorage.loadAsResource("litemall.png");
        String url = localStorage.generateUrl("litemall.png");
        System.out.println(("test file " + test));
        System.out.println(("store file " + (resource.getURI())));
        System.out.println(("generate url " + url));
        // localStorage.delete("litemall.png");
    }
}

