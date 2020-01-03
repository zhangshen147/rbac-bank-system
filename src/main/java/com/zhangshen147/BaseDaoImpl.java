package com.zhangshen147;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;


public class BaseDaoImpl<T> implements BaseDao<T> {

    @Override
    public int add(T t)  {

        Connection conn = null;
        PreparedStatement ps =null;
        String sql = null;
        int flag = 0; // 1 if success, 0 if fail
        Field nameField = null;
        Class clazz = t.getClass();
        try {
            nameField = clazz.getDeclaredField("name");
            nameField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        try{

            sql = "INSERT INTO rbac_db.users (name) " +
                    "VALUES (?)";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, (String) nameField.get(t));
            ps.execute();
            flag = 1;
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
            flag = 0;
        } finally {
            JdbcUtil.closeResource(conn,ps);
        }

        return flag;
    }

    @Override
    public void delete( int id) {

    }

    @Override
    public int update(T t) {
        return 0;
    }

    @Override
    public T query(Object[] params, Class<T> clazz) {
        return null;
    }

    @Override
    public T queryAll(Object[] params, Class<T> clazz) {
        return null;
    }


}
