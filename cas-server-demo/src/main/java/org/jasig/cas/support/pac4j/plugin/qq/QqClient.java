package org.jasig.cas.support.pac4j.plugin.qq;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tanxinzheng.cas.server.authentication.CombinedAccountVO;
import com.github.tanxinzheng.cas.server.authentication.CustomerManager;
import com.github.tanxinzheng.cas.server.authentication.LoginKeyType;
import org.jasig.cas.support.pac4j.plugin.wechat.WeiXinApi20;
import org.jasig.cas.support.pac4j.plugin.wechat.WeiXinAttributesDefinition;
import org.jasig.cas.support.pac4j.plugin.wechat.WeiXinOAuth20ServiceImpl;
import org.jasig.cas.support.pac4j.plugin.wechat.WeiXinProfile;
import org.jasig.cas.util.Pair;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.JsonHelper;
import org.scribe.model.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用于处理cas与微信的oauth2通信
 * Created by Jeng on 16/2/21.
 */
public class QqClient extends BaseOAuth20Client<WeiXinProfile> {
    private final static WeiXinAttributesDefinition WEI_XIN_ATTRIBUTES = new WeiXinAttributesDefinition();

    @Autowired
    private CustomerManager customerManager;

    public QqClient(){}

    public QqClient(final String key, final String secret){
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected BaseClient<OAuthCredentials, WeiXinProfile> newClient() {
        // TODO
        QqClient newClient = new QqClient();
        return newClient;
    }

    @Override
    protected void internalInit() {
        // TODO
        super.internalInit();
        WeiXinApi20 api = new WeiXinApi20();
        this.service = new WeiXinOAuth20ServiceImpl(api, new OAuthConfig(this.key, this.secret, this.callbackUrl, SignatureType.Header, null, null),
                this.connectTimeout, this.readTimeout, this.proxyHost,this.proxyPort);
    }

    @Override
    protected String getProfileUrl() {
        // TODO Auto-generated method stub
        // eg.google2Client:return "https://www.googleapis.com/oauth2/v2/userinfo";
        return "https://api.weixin.qq.com/sns/userinfo";
    }

    @Override
    protected WeiXinProfile extractUserProfile(String body) {
        WeiXinProfile weiXinProfile = new WeiXinProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (null != json) {
            for(final String attribute : WEI_XIN_ATTRIBUTES.getPrincipalAttributes()){
                weiXinProfile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }

            /** 绑定账号到系统 */
            String openId = (String) weiXinProfile.getAttributes().get("openid");
            String nickName = (String) weiXinProfile.getAttributes().get("nickname");
            CombinedAccountVO combinedAccount = generateAccount(openId, LoginKeyType.WECHAT, nickName);
            Pair<Long,String> suidAndLoginName = customerManager.bindAccount(combinedAccount);
            weiXinProfile.addAttribute("suid", suidAndLoginName.getFirst());
            weiXinProfile.setId(suidAndLoginName.getSecond());
        }
        return weiXinProfile;
    }

    /**
     * 需求state元素
     */
    @Override
    protected boolean requiresStateParameter() {
        return false;
    }

    @Override // Cancelled 取消
    protected boolean hasBeenCancelled(WebContext context) {
        return false;
    }

    @Override
    protected String sendRequestForData(final Token accessToken, final String dataUrl) {
        logger.debug("accessToken : {} / dataUrl : {}", accessToken, dataUrl);
        final long t0 = System.currentTimeMillis();
        final ProxyOAuthRequest request = createProxyRequest(dataUrl);
        this.service.signRequest(accessToken, request);

        final Response response = request.send();
        final int code = response.getCode();
        final String body = response.getBody();
        final long t1 = System.currentTimeMillis();
        logger.debug("Request took : " + (t1 - t0) + " ms for : " + dataUrl);
        logger.debug("response code : {} / response body : {}", code, body);
        if (code != 200) {
            logger.error("Failed to get data, code : " + code + " / body : " + body);
            throw new HttpCommunicationException(code, body);
        }
        return body;
    }

    private CombinedAccountVO generateAccount(String openId,LoginKeyType keyType, String nickName){
        CombinedAccountVO vo = new CombinedAccountVO();
        vo.setLoginKey(openId);
        vo.setKeyType(keyType);
        vo.setNickName(nickName);
        return vo;
    }
}
