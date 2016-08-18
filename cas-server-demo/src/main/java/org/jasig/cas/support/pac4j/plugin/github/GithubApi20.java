package org.jasig.cas.support.pac4j.plugin.github;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;

/**
 * 用于定义获取微信返回的code与access_token
 * Created by Jeng on 16/2/21.
 */
public class GithubApi20 extends DefaultApi20 {
    private static final String GITHUB_AUTHORIZE_URL = "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&state=xmomen&scope=user";

    @Override
    public AccessTokenExtractor getAccessTokenExtractor()
    {
        return new JsonTokenExtractor();
    }

    @Override
    public Verb getAccessTokenVerb()
    {
        return Verb.POST;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://github.com/login/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        return String.format(GITHUB_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
}
