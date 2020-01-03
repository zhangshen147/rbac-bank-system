package edu.bjut.test;

import com.zhangshen147.BaseDaoImpl;
import org.junit.Assert;
import org.junit.Test;

public class DaoTest {

    @Test
    public void addTest(){
        BaseDaoImpl<UserRole> dao = new BaseDaoImpl<>();
        dao.setProperties("dao_UserRole.properties");

        UserRole ur = new UserRole();
        ur.setUser_id(1);
        ur.setRole_id(3);

        int flag;

        flag = dao.add(ur);
        Assert.assertEquals(1,flag);
    }

    @Test
    public void deleteTest(){
        BaseDaoImpl<UserRole> dao = new BaseDaoImpl<>();
        dao.setProperties("dao_UserRole.properties");

        dao.delete(5);
    }

    @Test
    public void updateTest(){
        BaseDaoImpl<User> dao = new BaseDaoImpl<>();
        dao.setProperties("dao_User.properties");

        User u = new User();
        u.setName("'Bob'");
        u.setStatus(1);
        int flag;
        flag = dao.update(2,u);
        Assert.assertEquals(1,flag);
    }

    @Test
    public void queryTest() {
        // TODO queryTest
    }

    @Test
    public void queryAllTest() {
        // TODO queryAllTest
    }
}
