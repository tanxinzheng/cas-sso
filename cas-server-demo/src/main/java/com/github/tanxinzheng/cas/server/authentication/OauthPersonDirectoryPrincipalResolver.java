package com.github.tanxinzheng.cas.server.authentication;

import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.PrincipalResolver;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.jasig.cas.support.pac4j.authentication.principal.ClientCredential;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Jeng on 16/2/21.
 */
public class OauthPersonDirectoryPrincipalResolver implements PrincipalResolver {
    /** Log instance. */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean returnNullIfNoAttributes = false;

    public void setReturnNullIfNoAttributes(final boolean returnNullIfNoAttributes) {
        this.returnNullIfNoAttributes = returnNullIfNoAttributes;
    }

    @Override
    public Principal resolve(Credential credential) {
        logger.debug("Attempting to resolve a principal...");

        if (credential instanceof ClientCredential){
            // do nothing
        } else {
            throw new RuntimeException("用户数据转换异常!");
        }

        ClientCredential oauthCredential = (ClientCredential) credential;

        String principalId = oauthCredential.getUserProfile().getId();

        if (principalId == null) {
            logger.debug("Got null for extracted principal ID; returning null.");
            return null;
        }

        logger.debug("Creating SimplePrincipal for [{}]", principalId);
        UserProfile userProfile = oauthCredential.getUserProfile();
        final Map<String, Object> attributes = userProfile.getAttributes();

        if (attributes == null & !this.returnNullIfNoAttributes) {
            return new SimplePrincipal(principalId);
        }

        if (attributes == null) {
            return null;
        }

        return new SimplePrincipal(principalId, attributes);
    }

    @Override
    public boolean supports(Credential credential) {
        return true;
    }
}
