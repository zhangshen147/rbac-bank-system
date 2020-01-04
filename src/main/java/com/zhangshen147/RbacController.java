package com.zhangshen147;


import java.util.Random;

public class RbacController {
    public static final int PERMISSION_READ = 0;
    public static final int PERMISSION_WRITE = 1;
    public static final int PERMISSION_BOTH = 2;

    /**
     * 测试某个用户是否具有对某个文件的访问权限
     * @param user 当前登录的用户
     * @param file 准备进行访问的文件
     * @param level 进行访问的类型（READ/WRITE/BOTH）
     * @return true表示具备该权限，false表示不具备该权限
     */
    public static boolean testPermission(String user, String file, int level){
        // TODO
        // 暂时随机shengcheng
        Random random = new Random();
        return random.nextBoolean();
    }
}
