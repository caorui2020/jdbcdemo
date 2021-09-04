package com.bjsxt.jdbc;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
//        String sql = "select * from t1";
//        List<User> userList = DBUtil.executeQuery(sql, null, User.class);
//        userList.forEach(System.out::println);
//        String sql = "insert into t1 (uid,name) values (?,?)";
//        Object[] param = new Object[]{110,"test10"};
//        int i = DBUtil.executeUpdate(sql, param);
//        System.out.println(i);
        String sql = "update t1 set name = ? where id = ?";
        Object[] param = new Object[]{"hello",10};
        int i = DBUtil.executeUpdate(sql, param);
        System.out.println(i);


    }
}
