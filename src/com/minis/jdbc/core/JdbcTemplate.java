package com.minis.jdbc.core;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class JdbcTemplate {

    private DataSource dataSource;

    public JdbcTemplate() {
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Object query(StatementCallback statementCallback) {
        Connection con = null;
        Statement stmt = null;

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            return statementCallback.doInStatement(stmt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
                if (null != con) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public Object query(String sql, Object[] args, PreparedStatementCallback preparedStatementCallback) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            //初始化连接
            con = dataSource.getConnection();
            stmt = con.prepareStatement(sql);
            //通过argumentSetter统一设置参数值
            ArgumentPreparedStatementSetter argumentSetter = new ArgumentPreparedStatementSetter(args);
            argumentSetter.setValues(stmt);
            return preparedStatementCallback.doInPreparedStatement(stmt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
                if (null != con) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {
        RowMapperResultSetExtractor<T> resultExtractor = new RowMapperResultSetExtractor<>(rowMapper);
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            //建立数据库连接
            con = dataSource.getConnection();
            //准备SQL命令语句
            pstmt = con.prepareStatement(sql);
            //设置参数
            ArgumentPreparedStatementSetter argumentSetter = new ArgumentPreparedStatementSetter(args);
            argumentSetter.setValues(pstmt);
            //执行语句
            rs = pstmt.executeQuery();
            //数据库结果集映射为对象列表，返回
            return resultExtractor.extractData(rs);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                pstmt.close();
                con.close();
            } catch (Exception e) {
            }
        }
        return null;
    }
}