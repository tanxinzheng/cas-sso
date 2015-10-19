package com.github.tanxinzheng.cas.server.authentication;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;
import java.security.GeneralSecurityException;

/**
 * Created by Jeng on 2015/10/17.
 */
public class DefaultDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    @NotNull
    private String sql;

    @NotNull
    private SaltSource saltSource;

    /**
     * Authenticates a username/password credential by an arbitrary strategy.
     *
     * @param transformedCredential the credential object bearing the transformed username and password.
     * @return HandlerResult resolved from credential on authentication success or null if no principal could be resolved
     * from the credential.
     * @throws java.security.GeneralSecurityException          On authentication failure.
     * @throws org.jasig.cas.authentication.PreventedException On the indeterminate case when authentication is prevented.
     */
    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential)
            throws GeneralSecurityException, PreventedException {
        final String username = credential.getUsername();
        final String password = credential.getPassword();
        String salt = (String) saltSource.getSalt(credential);
        String encryptedPassword = MD5Utils.encrypt(password, salt);
        try {
            final String dbPassword = getJdbcTemplate().queryForObject(this.sql, String.class, username, username, username);
            if (!dbPassword.equals(encryptedPassword)) {
                throw new FailedLoginException("Password does not match value on record.");
            }
        } catch (final IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with SQL query");
            } else {
                throw new FailedLoginException("Multiple records found for " + username);
            }
        } catch (final DataAccessException e) {
            throw new PreventedException("SQL exception while executing query for " + username, e);
        }
        return createHandlerResult(credential, new SimplePrincipal(username), null);
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public SaltSource getSaltSource() {
        return saltSource;
    }

    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }
}
