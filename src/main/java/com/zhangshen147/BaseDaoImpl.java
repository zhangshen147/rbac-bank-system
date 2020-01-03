package com.zhangshen147;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class BaseDaoImpl<T> implements BaseDao<T> {

    Connection conn = null;
    PreparedStatement ps =null;
    ResultSet rs = null;
    String sql = null;
    String tableName = null;

    public void setProperties(String properties){
        InputStream propInput = BaseDaoImpl.class.getClassLoader().getResourceAsStream(properties);
        Properties prop = new Properties();
        try {
            assert propInput != null;
            prop.load(propInput);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tableName = prop.getProperty("tableName");

    }

    @Override
    public int add(T t)  {
        int flag; // 1 if success, 0 if fail

        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder fieldNameBuilder = new StringBuilder("(");
        StringBuilder fieldValueBuilder = new StringBuilder("(");
        for (int i = 0; i<fields.length;i++) {
            try {
                fields[i].setAccessible(true);
                fieldNameBuilder.append(fields[i].getName());
                fieldValueBuilder.append(fields[i].get(t).toString());
                if(i!=fields.length-1){
                    fieldNameBuilder.append(",");
                    fieldValueBuilder.append(",");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        fieldNameBuilder.append(")");
        fieldValueBuilder.append(")");

        try{

            sql = "INSERT INTO " +tableName+fieldNameBuilder.toString()+
                    " VALUES " + fieldValueBuilder.toString();
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.execute();
            flag = 1;
        } catch (SQLException e) {
            e.printStackTrace();
            flag = 0;
        } finally {
            JdbcUtil.closeResource(conn,ps);
        }

        return flag;
    }

    @Override
    public void delete( int id) {
        try{

            sql = "DELETE FROM "+tableName+" WHERE "+tableName+".id = "+ id +";" ;
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.closeResource(conn,ps);
        }
    }

    @Override
    public int update(int id,T t) {
        int flag; // 1 if success, 0 if fail

        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " +tableName+" SET ");
        for (int i = 0; i<fields.length;i++) {
            try {
                fields[i].setAccessible(true);
                sqlBuilder.append(fields[i].getName());
                sqlBuilder.append(" = ");
                sqlBuilder.append(fields[i].get(t).toString());
                if(fields.length - 1 != i){
                    sqlBuilder.append(",");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        sqlBuilder.append(" WHERE ").append(tableName).append(".id = ").append(id).append(";");

        try{
            sql = sqlBuilder.toString();
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.execute();
            flag = 1;
        } catch (SQLException e) {
            e.printStackTrace();
            flag = 0;
        } finally {
            JdbcUtil.closeResource(conn,ps);
        }

        return flag;
    }

    @Override
    public T queryByID(int id, Class<T> clazz, Object[] params) {
        T t = null;
        try{
            sql = "SELECT * FROM "+tableName+" WHERE "+tableName+".id = "+ id +";" ;
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if(rs.next()){
                t = clazz.getDeclaredConstructor().newInstance();
                // TODO Finish query methods with reflection
            }
        } catch (SQLException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.closeResource(conn,ps);
        }

        return t;
    }

    @Override
    public T[] queryAll( Class<T> clazz, Object[] params) {
        return null;
    }


}
