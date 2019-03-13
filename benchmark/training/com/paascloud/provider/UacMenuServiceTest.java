package com.paascloud.provider;


import com.paascloud.provider.model.vo.MenuVo;
import com.paascloud.provider.service.UacMenuService;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;


public class UacMenuServiceTest extends PaasCloudUacApplicationTests {
    @Resource
    private UacMenuService uacMenuService;

    @Test
    public void findMenuListByUserIdTest() {
        List<MenuVo> menuVoListByUserId = uacMenuService.getMenuVoList(1L, 1L);
        logger.info("findByLoginNameTest = {}", menuVoListByUserId);
    }
}

