/**
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 */
package org.dromara.raincat.admin.service.recover;


import SpringBootTest.WebEnvironment;
import org.dromara.raincat.admin.page.CommonPager;
import org.dromara.raincat.admin.page.PageParameter;
import org.dromara.raincat.admin.query.RecoverTransactionQuery;
import org.dromara.raincat.admin.service.RecoverTransactionService;
import org.dromara.raincat.admin.vo.TransactionRecoverVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * <p>Description:</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @unknown 2017/10/20 16:01
 * @since JDK 1.8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ZookeeperRecoverTransactionServiceImplTest {
    @Autowired
    private RecoverTransactionService recoverTransactionService;

    @Test
    public void listByPage() throws Exception {
        RecoverTransactionQuery query = new RecoverTransactionQuery();
        query.setApplicationName("alipay-service");
        PageParameter pageParameter = new PageParameter(1, 8);
        query.setPageParameter(pageParameter);
        final CommonPager<TransactionRecoverVO> voCommonPager = recoverTransactionService.listByPage(query);
    }
}

