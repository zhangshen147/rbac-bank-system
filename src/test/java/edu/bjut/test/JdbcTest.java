package edu.bjut.test;

import com.zhangshen147.JdbcUtil;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;

public class JdbcTest {
    @Test
    public void jdbcImplTest(){
        Connection conn = JdbcUtil.getConnection();
        Assert.assertNotNull(conn);
    }
}
