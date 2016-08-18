package org.jasig.cas.support.pac4j.plugin.github;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于获取微信返回的access_token
 * Created by Jeng on 16/2/21.
 */
public class GithubJsonTokenExtractor {

    private Pattern accessTokenPattern = Pattern.compile("\"access_token\":\\s*\"(\\S*?)\"");

    public Token extract(String response){
        Preconditions.checkEmptyString(response, "Cannot extract a token from a null or empty String");
        Matcher matcher = accessTokenPattern.matcher(response);
        if(matcher.find()){
            return new Token(matcher.group(1), "", response);
        }
        else{
            throw new OAuthException("Cannot extract an acces token. Response was: " + response);
        }
    }
}
