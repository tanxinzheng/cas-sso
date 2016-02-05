package com.github.tanxinzheng.cas.server.support;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AttributeNamedPersonImpl;
import org.jasig.services.persondir.support.StubPersonAttributeDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeng on 16/2/4.
 */
public class MultipleAttributeUserDao extends StubPersonAttributeDao {

    @Override
    public IPersonAttributes getPerson(String uid) {
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        attributes.put("userid", Collections.singletonList((Object)uid));
        attributes.put("username", Collections.singletonList((Object)"测试username"));
       // attributes.put("password", Collections.singletonList((Object)"测试password"));
        attributes.put("email", Collections.singletonList((Object) "test@163.com"));
        return new AttributeNamedPersonImpl(attributes);
    }
}
