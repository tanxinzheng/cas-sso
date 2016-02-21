package com.github.tanxinzheng.cas.server.authentication;

/**
 * Created by Jeng on 16/2/21.
 */
public class CombinedAccountVO {

    private String loginKey;
    private LoginKeyType keyType;
    private String nickName;

    public String getLoginKey() {
        return loginKey;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

    public LoginKeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(LoginKeyType keyType) {
        this.keyType = keyType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
