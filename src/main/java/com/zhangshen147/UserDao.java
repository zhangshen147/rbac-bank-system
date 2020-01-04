package com.zhangshen147;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDao {

    public static String sql;
    public static Connection conn;
    public static PreparedStatement ps;
    public static ResultSet rs;

    public static int add(User user)  {
        int flag; // 1 if success, 0 if fail
        try{
            sql = "INSERT INTO rbac_db.users (id, name) VALUES (?,?)";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,user.getId());
            ps.setString(2,user.getName());
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

    public static int delete(User user){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "DELETE FROM rbac_db.users WHERE id=? AND name=?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,user.getId());
            ps.setString(2,user.getName());
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

    public static int update(User user,int id){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "UPDATE rbac_db.users SET id=?,name=? WHERE id=?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,user.getId());
            ps.setString(2,user.getName());
            ps.setInt(3,id);
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

    public static User queryById(int id) {
        User result;
        try{
            sql = "SELECT * FROM rbac_db.users WHERE id = ?";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            rs = ps.executeQuery();
            if(rs.next()){
                result = new User();
                result.setId(rs.getInt(1));
                result.setName(rs.getString(2));
            }else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = null;
        } finally {
            JdbcUtil.closeResource(conn, rs, ps);
        }
        return result;
    }

    public static ArrayList<User> queryAll() {
        ArrayList<User> result = null;
        try{
            sql = "SELECT * FROM rbac_db.users ;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            rs.last();
            int rsSize = rs.getRow();
            rs.beforeFirst();
            if(rsSize>0) {
                result = new ArrayList<>(rsSize);
            }
            while(rs.next()){
                User tmp = new User();
                tmp.setId(rs.getInt(1));
                tmp.setName(rs.getString(2));
                assert result != null;
                result.add(tmp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = null;
        } finally {
            JdbcUtil.closeResource(conn, rs, ps);
        }
        return result;
    }
}
