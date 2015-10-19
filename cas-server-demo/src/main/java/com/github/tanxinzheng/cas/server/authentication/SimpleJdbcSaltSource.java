package com.github.tanxinzheng.cas.server.authentication;

import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * Created by Jeng on 2015/10/17.
 */
public class SimpleJdbcSaltSource implements SaltSource {

    @NotNull
    private String sql;

    @NotNull
    private JdbcTemplate jdbcTemplate;

    @NotNull
    private DataSource dataSource;

    /**
     * Returns the salt to use for the indicated user.
     *
     * @param object
     * @return
     */
    @Override
    public Object getSalt(UsernamePasswordCredential credential) {
        String username = credential.getUsername();
        final String salt = getJdbcTemplate().queryForObject(this.sql, String.class, username, username, username);
        return salt;
    }

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    protected String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
