package com.github.tanxinzheng.cas.server.authentication;

import org.jasig.cas.authentication.UsernamePasswordCredential;

/**
 * Provides alternative sources of the salt to use for encoding passwords.
 * Created by Jeng on 2015/10/17.
 */
public interface SaltSource {

    /**
     * Returns the salt to use for the indicated user.
     * @param credential
     * @return
     */
    public Object getSalt(UsernamePasswordCredential credential);
}
