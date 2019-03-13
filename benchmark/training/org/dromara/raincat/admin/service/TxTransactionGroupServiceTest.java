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
package org.dromara.raincat.admin.service;


import SpringBootTest.WebEnvironment;
import java.util.List;
import org.assertj.core.util.Lists;
import org.dromara.raincat.admin.page.CommonPager;
import org.dromara.raincat.admin.page.PageParameter;
import org.dromara.raincat.admin.query.TxTransactionQuery;
import org.dromara.raincat.admin.vo.TxTransactionGroupVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * <p>Description:</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @unknown 2017/10/18 16:38
 * @since JDK 1.8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TxTransactionGroupServiceTest {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionGroupServiceTest.class);

    @Autowired
    private TxTransactionGroupService txTransactionGroupService;

    @Test
    public void listByPage() throws Exception {
        PageParameter pageParameter = new PageParameter(1, 3);
        TxTransactionQuery query = new TxTransactionQuery();
        query.setPageParameter(pageParameter);
        TxTransactionGroupServiceTest.LOGGER.info(query.toString());
        final CommonPager<TxTransactionGroupVO> commonPager = txTransactionGroupService.listByPage(query);
        final List<TxTransactionGroupVO> dataList = commonPager.getDataList();
        Assert.assertNotNull(dataList);
    }

    @Test
    public void batchRemove() {
        List<String> txGroupIds = Lists.newArrayList();
        txGroupIds.add("1408937230");
        txGroupIds.add("353342572");
        final Boolean aBoolean = txTransactionGroupService.batchRemove(txGroupIds);
        Assert.assertNotNull(aBoolean);
    }
}

