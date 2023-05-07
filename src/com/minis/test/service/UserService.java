package com.minis.test.service;

import com.minis.batis.SqlSession;
import com.minis.batis.SqlSessionFactory;
import com.minis.beans.factory.annotation.Autowired;
import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.RowMapper;
import com.minis.test.entity.User;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


//    public User getUserInfo(int userId) {
//        String sql = "select id,name,birthday from user where id =" + userId;
//        JdbcTemplate jdbcTemplate = new UserJdbcImpl();
//        return (User) jdbcTemplate.query(sql);
//    }


//    public User getUserInfo(int userId) {
//        String sql = "select id,name,birthday from user where id =" + userId;
//        return (User) jdbcTemplate.query(stmt -> {
//            ResultSet rs = stmt.executeQuery(sql);
//            User rtnUser = null;
//            if (rs.next()) {
//                rtnUser = new User();
//                rtnUser.setId(userId);
//                rtnUser.setName(rs.getString("name"));
//                rtnUser.setBirthday(new Date(rs.getDate("birthday").getTime()));
//            }
//            return rtnUser;
//        });
//    }

    public User getUserInfo(int userId) {
        String sql = "select id,name,birthday from user where id =" + userId;
        return (User) jdbcTemplate.query(sql, new Object[]{userId},
                (stmt -> {
                    ResultSet rs = stmt.executeQuery();
                    User rtnUser = null;
                    if (rs.next()) {
                        rtnUser = new User();
                        rtnUser.setId(userId);
                        rtnUser.setName(rs.getString("name"));
                        rtnUser.setBirthday(new java.util.Date(rs.getDate("birthday").getTime()));
                    }
                    return rtnUser;
                }));
    }

    public List<User> getUsers(int userid) {
        final String sql = "select id, name,birthday from users where id>?";
        return (List<User>)jdbcTemplate.query(sql, new Object[]{new Integer(userid)},
                new RowMapper<User>(){
                    public User mapRow(ResultSet rs, int i) throws SQLException {
                        User rtnUser = new User();
                        rtnUser.setId(rs.getInt("id"));
                        rtnUser.setName(rs.getString("name"));
                        rtnUser.setBirthday(new java.util.Date(rs.getDate("birthday").getTime()));
                        return rtnUser;
                    }
                }
        );
    }


    public User getUserById(int userId) {
        String sqlId = "com.minis.test.entity.User.getUserInfo";
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return (User) sqlSession.selectOne(sqlId, new Object[]{userId}, stmt -> {
            ResultSet rs = stmt.executeQuery();
            User rtnUser = null;
            if (rs.next()) {
                rtnUser = new User();
                rtnUser.setId(userId);
                rtnUser.setName(rs.getString("name"));
                rtnUser.setBirthday(new java.util.Date(rs.getDate("birthday").getTime()));
            }
            return rtnUser;
        });
    }
}
