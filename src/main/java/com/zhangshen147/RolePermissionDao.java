package com.zhangshen147;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RolePermissionDao {
    public static String sql;
    public static Connection conn;
    public static PreparedStatement ps;
    public static ResultSet rs;

    public static int add(RolePermission rolePermission)  {
        int flag; // 1 if success, 0 if fail
        try{
            sql = "INSERT INTO rbac_db.role_permissions (id, role_id, permission_id) VALUES (?,?,?)";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,rolePermission.getId());
            ps.setInt(2,rolePermission.getRoleId());
            ps.setInt(3,rolePermission.getPermissionId());
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

    public static int delete(RolePermission rolePermission){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "DELETE FROM rbac_db.role_permissions WHERE id=? AND role_id=? AND permission_id = ?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,rolePermission.getId());
            ps.setInt(2,rolePermission.getRoleId());
            ps.setInt(3,rolePermission.getPermissionId());
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

    public static int update(RolePermission rolePermission,int id){
        int flag; // 1 if success, 0 if fail
        try{
            sql = "UPDATE rbac_db.role_permissions SET id=?,role_id=?,permission_id=? WHERE id=?;";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,rolePermission.getId());
            ps.setInt(2,rolePermission.getRoleId());
            ps.setInt(3,rolePermission.getPermissionId());
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

    public static RolePermission queryById(int id) {
        RolePermission result;
        try{
            sql = "SELECT * FROM rbac_db.role_permissions WHERE id = ?";
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            rs = ps.executeQuery();
            if(rs.next()){
                result = new RolePermission();
                result.setId(rs.getInt(1));
                result.setRoleId(rs.getInt(2));
                result.setPermissionId(rs.getInt(3));
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

    public static ArrayList<RolePermission> queryAll() {
        ArrayList<RolePermission> result = null;
        try{
            sql = "SELECT * FROM rbac_db.role_permissions ;";
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
                RolePermission tmp = new RolePermission();
                tmp.setId(rs.getInt(1));
                tmp.setRoleId(rs.getInt(2));
                tmp.setPermissionId(rs.getInt(3));
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
