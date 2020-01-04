package edu.bjut.test;

import com.zhangshen147.Role;
import com.zhangshen147.RoleDao;
import com.zhangshen147.User;
import com.zhangshen147.UserDao;
import org.junit.Assert;
import org.junit.Test;

public class DaoTest {
    @Test
    public void UserDaoTest(){
        User user = new User();
        user.setName("Haze");
        user.setId(10086);
        int flag = UserDao.add(user);
        Assert.assertEquals(1,flag);
        User copy = UserDao.queryById(10086);
        Assert.assertNotNull(copy);
        System.out.println(copy.getName());
        int flag2 = UserDao.delete(user);
        Assert.assertEquals(1,flag2);
    }

    @Test
    public void RoleDaoTest(){
        Role role = new Role();
        role.setName("Accountant");
        role.setId(76);
        int flag = RoleDao.add(role);
        Assert.assertEquals(1,flag);
        Role copy = RoleDao.queryById(role.getId());
        Assert.assertNotNull(copy);
        System.out.println(copy.getName());
        int flag2 = RoleDao.delete(role);
        Assert.assertEquals(1,flag2);
    }
}
