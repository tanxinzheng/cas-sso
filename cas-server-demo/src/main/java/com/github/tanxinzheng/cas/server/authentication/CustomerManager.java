package com.github.tanxinzheng.cas.server.authentication;

import org.jasig.cas.util.Pair;

/**
 * Created by Jeng on 16/2/21.
 */
public interface CustomerManager {

    public Pair bindAccount(CombinedAccountVO combinedAccountVO);
}
