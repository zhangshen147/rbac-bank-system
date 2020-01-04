package com.zhangshen147;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoleDao {

    public static String sql;
    public static Connection conn;
    public static PreparedStatement ps;
    public static ResultSet rs;

    public static int add(Role role)  {
        int flag; // 1 if success, 0 if fail
        try{
            sql = "INSERT INTO rbac_db.roles (id, name) VALUES (?,?)";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,role.getId());
            ps.setString(2,role.getName());
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

    public static int delete(Role role){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "DELETE FROM rbac_db.roles WHERE id=? AND name=?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,role.getId());
            ps.setString(2,role.getName());
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

    public static int update(Role role,int id){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "UPDATE rbac_db.roles SET id=?,name=? WHERE id=?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,role.getId());
            ps.setString(2,role.getName());
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

    public static Role queryById(int id) {
        Role result;
        try{
            sql = "SELECT * FROM rbac_db.roles WHERE id = ?";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            rs = ps.executeQuery();
            if(rs.next()){
                result = new Role();
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

    public static ArrayList<Role> queryAll() {
        ArrayList<Role> result = null;
        try{
            sql = "SELECT * FROM rbac_db.roles ;";
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
                Role tmp = new Role();
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
