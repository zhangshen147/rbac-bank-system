package com.zhangshen147;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PermissionDao {
    public static String sql;
    public static Connection conn;
    public static PreparedStatement ps;
    public static ResultSet rs;

    public static int add(Permission permission)  {
        int flag; // 1 if success, 0 if fail
        try{
            sql = "INSERT INTO rbac_db.permissions (id, title, urls) VALUES (?,?,?)";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,permission.getId());
            ps.setString(2,permission.getTitle());
            ps.setString(3,permission.getUrl());
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

    public static int delete(Permission permission){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "DELETE FROM rbac_db.permissions WHERE id=? AND title=? AND urls=?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,permission.getId());
            ps.setString(2,permission.getTitle());
            ps.setString(3,permission.getUrl());
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

    public static int update(Permission permission,int id){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "UPDATE rbac_db.permissions SET id=?,title=?,urls=? WHERE id=?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,permission.getId());
            ps.setString(2,permission.getTitle());
            ps.setString(3,permission.getUrl());
            ps.setInt(4,id);
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

    public static Permission queryById(int id) {
        Permission result;
        try{
            sql = "SELECT * FROM rbac_db.permissions WHERE id = ?";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            rs = ps.executeQuery();
            if(rs.next()){
                result = new Permission();
                result.setId(rs.getInt(1));
                result.setTitle(rs.getString(2));
                result.setUrl(rs.getString(3));
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

    public static ArrayList<Permission> queryAll() {
        ArrayList<Permission> result = null;
        try{
            sql = "SELECT * FROM rbac_db.permissions ;";
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
                Permission tmp = new Permission();
                tmp.setId(rs.getInt(1));
                tmp.setTitle(rs.getString(2));
                tmp.setUrl(rs.getString(3));
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
