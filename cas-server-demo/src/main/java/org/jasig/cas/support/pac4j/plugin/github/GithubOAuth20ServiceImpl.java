package org.jasig.cas.support.pac4j.plugin.github;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.*;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于添加获取access_token与用户信息添加参数并请求微信开发认证平台
 * Created by Jeng on 16/2/21.
 */
public class GithubOAuth20ServiceImpl extends ProxyOAuth20ServiceImpl {

    private static Pattern openIdPattern = Pattern.compile("\"openid\":\\s*\"(\\S*?)\"");

    public GithubOAuth20ServiceImpl(DefaultApi20 api, OAuthConfig config, int connectTimeout, int readTimeout, String proxyHost, int proxyPort) {
        super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort);
    }

    /**
     * 获取account_token的http请求参数添加
     */
    @Override
    public Token getAccessToken(final Token requestToken, final Verifier verifier) {
        final OAuthRequest request = new ProxyOAuthRequest(this.api.getAccessTokenVerb(),
                this.api.getAccessTokenEndpoint(), this.connectTimeout,
                this.readTimeout, this.proxyHost, this.proxyPort);
        request.addBodyParameter("client_id", this.config.getApiKey());
        request.addBodyParameter("client_secret", this.config.getApiSecret());
        request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
        request.addBodyParameter(OAuthConstants.REDIRECT_URI, this.config.getCallback());
        request.addBodyParameter("grant_type", "authorization_code");
        final Response response = request.send();
        return this.api.getAccessTokenExtractor().extract(response.getBody());
    }

    @Override
    public void signRequest(final Token accessToken, final OAuthRequest request) {
        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
        String response = accessToken.getRawResponse();
        Matcher matcher = openIdPattern.matcher(response);
        if(matcher.find()){
            request.addQuerystringParameter("openid", matcher.group(1));
        }
        else{
            throw new OAuthException("Github接口返回数据miss openid: " + response);
        }
    }
}
