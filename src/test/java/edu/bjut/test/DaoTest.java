package edu.bjut.test;

import com.zhangshen147.BaseDaoImpl;
import com.zhangshen147.JdbcUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;

public class DaoTest {

    Connection conn;
    Statement stmt;
    @Before
    public void setUp() throws Exception {
        conn = JdbcUtil.getConnection();
        String sql = "USE rbac_db;";
        stmt = conn.createStatement();
        stmt.execute(sql);
    }

    @Test
    public void addTest(){
        BaseDaoImpl<User> dao = new BaseDaoImpl<>();
        User user = new User();
        user.setName("Alice");
        user.setStatus(1);
        int flag;
        flag = dao.add(user);
        Assert.assertEquals(1,flag);
    }
}
