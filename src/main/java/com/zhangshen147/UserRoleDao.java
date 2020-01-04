package com.zhangshen147;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserRoleDao {
    public static String sql;
    public static Connection conn;
    public static PreparedStatement ps;
    public static ResultSet rs;

    public static int add(UserRole userRole)  {
        int flag; // 1 if success, 0 if fail
        try{
            sql = "INSERT INTO rbac_db.user_roles (id, user_id, role_id) VALUES (?,?,?)";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,userRole.getId());
            ps.setInt(2,userRole.getUserId());
            ps.setInt(3,userRole.getRoleId());
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

    public static int delete(UserRole userRole){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "DELETE FROM rbac_db.user_roles WHERE id=? AND user_id=? AND role_id = ?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,userRole.getId());
            ps.setInt(2,userRole.getUserId());
            ps.setInt(3,userRole.getRoleId());
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

    public static int update(UserRole userRole,int id){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "UPDATE rbac_db.user_roles SET id=?,user_id=?,role_id=? WHERE id=?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,userRole.getId());
            ps.setInt(2,userRole.getUserId());
            ps.setInt(3,userRole.getRoleId());
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

    public static UserRole queryById(int id) {
        UserRole result;
        try{
            sql = "SELECT * FROM rbac_db.user_roles WHERE id = ?";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            rs = ps.executeQuery();
            if(rs.next()){
                result = new UserRole();
                result.setId(rs.getInt(1));
                result.setUserId(rs.getInt(2));
                result.setRoleId(rs.getInt(3));
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

    public static ArrayList<UserRole> queryAll() {
        ArrayList<UserRole> result = null;
        try{
            sql = "SELECT * FROM rbac_db.user_roles ;";
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
                UserRole tmp = new UserRole();
                tmp.setId(rs.getInt(1));
                tmp.setUserId(rs.getInt(2));
                tmp.setRoleId(rs.getInt(3));
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
