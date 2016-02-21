CAS4 Overlay Template
============================

## 支持RememberMe功能
1. cas-servlet.xml修改
```xml
<root>
...
    <!-- 20090220 rememberMe start -->
    <!--
    <bean id="authenticationViaFormAction" class="org.jasig.cas.web.flow.AuthenticationViaFormAction"
        p:centralAuthenticationService-ref="centralAuthenticationService"
        p:warnCookieGenerator-ref="warnCookieGenerator" />
    -->
    <bean id="authenticationViaFormAction" class="org.jasig.cas.web.flow.AuthenticationViaFormAction"
        p:centralAuthenticationService-ref="centralAuthenticationService"
        p:formObjectClass="org.jasig.cas.authentication.principal.RememberMeUsernamePasswordCredentials"
        p:formObjectName="credentials"
        p:validator-ref="UsernamePasswordCredentialsValidator"
        p:warnCookieGenerator-ref="warnCookieGenerator" />
    <!-- 20090220 rememberMe end -->
...
</root>
```
2. 修改deployerConfigContext.xml文件
```xml
<root>
...
    <bean id="authenticationManager" class="org.jasig.cas.authentication.AuthenticationManagerImpl">
        <property name="authenticationMetaDataPopulators">
            <list>
                <bean class="org.jasig.cas.authentication.principal.RememberMeAuthenticationMetaDataPopulator" />
            </list>
        </property>
    </bean>
...
</root>
```
3. 修改login-webflow.xml文件
```xml
<root>
...
    <var name="credential" class="org.jasig.cas.authentication.RememberMeUsernamePasswordCredential" />
...
</root>
```

## 支持Memcached

1. 修改pom.xml添加memcached依赖包
```
<dependency>
        <groupId>org.jasig.cas</groupId>
        <artifactId>cas-server-integration-memcached</artifactId>
        <version>${cas.version}</version>
        <scope>compile</scope>
</dependency>
```
2. 修改ticketRegistry.xml文件
```xml
<root>
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:c="http://www.springframework.org/schema/c"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/util
       http://www.springframework.org/schema/util/spring-util.xsd">
    <description>
    	Configuration for the default TicketRegistry which stores the tickets in-memory and cleans them out as specified intervals.
    </description>
  <!-- Ticket Registry -->
    <bean id="ticketRegistry"
          class="org.jasig.cas.ticket.registry.MemCacheTicketRegistry"
          c:client-ref="memcachedClient"
          c:ticketGrantingTicketTimeOut="${tgt.maxTimeToLiveInSeconds}"
          c:serviceTicketTimeOut="${st.timeToKillInSeconds}" />
    <bean id="kryoTranscoder"
          class="org.jasig.cas.ticket.registry.support.kryo.KryoTranscoder"
          init-method="initialize"
          c:initialBufferSize="8192" />
    <bean id="memcachedClient" class="net.spy.memcached.spring.MemcachedClientFactoryBean"
          p:servers="${memcached.servers}"
          p:protocol="${memcached.protocol}"
          p:locatorType="${memcached.locatorType}"
          p:failureMode="${memcached.failureMode}"
          p:transcoder-ref="kryoTranscoder">
        <property name="hashAlg">
            <util:constant static-field="net.spy.memcached.DefaultHashAlgorithm.${memcached.hashAlgorithm}" />
        </property>
    </bean>
</beans>
</root>
```
3. 修改cas.properties文件，添加Memcached服务器配置
```
# memcached server configuration
memcached.servers=localhost:11211,localhost:11212
memcached.hashAlgorithm=FNV1_64_HASH
memcached.protocol=BINARY
memcached.locatorType=ARRAY_MOD
memcached.failureMode=Redistribute
```

## 支持微信OAuth2协议第三方登录

1. 在pom.xml中添加oauth2,pac4j-oauth依赖包
```
<!--  注：cas-server-support-pac4j包中内置依赖pac4j-core.jar，添加pac4j-oauth时，需知道pac4j-core的版本，保持版本一致防止出现版本异常问题  -->
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-pac4j</artifactId>
    <version>${cas.version}</version>
</dependency>
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oauth</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

2. 添加微信oauth2认证与cas集成所需要的自定义类文件，文件详细内容请点击以下文件查看

- oauth2第三方登录公共类
[com.github.tanxinzheng.cas.server.authentication.LoginKeyType](./src/main/java/com.github.tanxinzheng.cas.server.authentication.LoginKeyType)
[com.github.tanxinzheng.cas.server.authentication.CustomerManager](./src/main/java/com.github.tanxinzheng.cas.server.authentication.CustomerManager)
[com.github.tanxinzheng.cas.server.authentication.CombinedAccountVO](./src/main/java/com.github.tanxinzheng.cas.server.authentication.CombinedAccountVO)
[com.github.tanxinzheng.cas.server.authentication.OauthPersonDirectoryPrincipalResolver](./src/main/java/com.github.tanxinzheng.cas.server.authentication.OauthPersonDirectoryPrincipalResolver)

- 微信自定义类
[org.jasig.cas.support.pac4j.plugin.wechat.WeiXinApi20](./src/main/java/org.jasig.cas.support.pac4j.plugin.wechat.WeiXinApi20)
[org.jasig.cas.support.pac4j.plugin.wechat.WeiXinAttributesDefinition](./src/main/java/org.jasig.cas.support.pac4j.plugin.wechat.WeiXinAttributesDefinition)
[org.jasig.cas.support.pac4j.plugin.wechat.WeiXinClient](./src/main/java/org.jasig.cas.support.pac4j.plugin.wechat.WeiXinClient)
[org.jasig.cas.support.pac4j.plugin.wechat.WeiXinJsonTokenExtractor](./src/main/java/org.jasig.cas.support.pac4j.plugin.wechat.WeiXinJsonTokenExtractor)
[org.jasig.cas.support.pac4j.plugin.wechat.WeiXinOAuth20ServiceImpl](./src/main/java/org.jasig.cas.support.pac4j.plugin.wechat.WeiXinOAuth20ServiceImpl)
[org.jasig.cas.support.pac4j.plugin.wechat.WeiXinProfile](./src/main/java/org.jasig.cas.support.pac4j.plugin.wechat.WeiXinProfile)

