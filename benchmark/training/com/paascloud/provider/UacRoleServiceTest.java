package com.paascloud.provider;


import GlobalConstant.Sys.SUPER_MANAGER_LOGIN_NAME;
import com.paascloud.provider.model.domain.UacRole;
import com.paascloud.provider.service.UacRoleService;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * The class Uac role service test.
 *
 * @author paascloud.net@gmail.com
 */
public class UacRoleServiceTest extends PaasCloudUacApplicationTests {
    @Resource
    private UacRoleService uacRoleService;

    /**
     * Find by login name test.
     */
    @Test
    public void findByLoginNameTest() {
        UacRole admin = uacRoleService.findByRoleCode(SUPER_MANAGER_LOGIN_NAME);
        logger.info("findByLoginNameTest = {}", admin);
    }
}

