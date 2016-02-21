package org.jasig.cas.support.pac4j.plugin.wechat;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * 用于添加返回用户信息
 * Created by Jeng on 16/2/21.
 */
public class WeiXinProfile extends OAuth20Profile {

    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return new WeiXinAttributesDefinition();
    }
}
