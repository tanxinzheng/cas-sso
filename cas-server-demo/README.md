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

## 支持自定义登录页面主题
1. 创建demo主题目录（demo为自定义主题名称），复制view/jsp/default文件夹下所有文件至view/jsp/demo下

2. 复制默认主题配置文件：cas-theme-default.properties，default_views.properties，如修改为demo主题cas-theme-demo.properties，demo_views.properties，并修改jsp路径
```
# 修改前
...
casLoginView.(class)=org.springframework.web.servlet.view.JstlView
casLoginView.url=/WEB-INF/view/jsp/default/ui/casLoginView.jsp

casLoginMessageView.(class)=org.springframework.web.servlet.view.JstlView
casLoginMessageView.url=/WEB-INF/view/jsp/default/ui/casLoginMessageView.jsp

casLoginConfirmView.(class)=org.springframework.web.servlet.view.JstlView
casLoginConfirmView.url=/WEB-INF/view/jsp/default/ui/casConfirmView.jsp
...
－－－－－－－－－－－－－－－－－－－－
修改后
...
casLoginView.(class)=org.springframework.web.servlet.view.JstlView
casLoginView.url=/WEB-INF/view/jsp/demo/ui/casLoginView.jsp

casLoginMessageView.(class)=org.springframework.web.servlet.view.JstlView
casLoginMessageView.url=/WEB-INF/view/jsp/demo/ui/casLoginMessageView.jsp

casLoginConfirmView.(class)=org.springframework.web.servlet.view.JstlView
casLoginConfirmView.url=/WEB-INF/view/jsp/demo/ui/casConfirmView.jsp
...
```

3. 修改cas.properties文件样式主题配置参数，启用demo主题，重启服务即可。
```
...
cas.themeResolver.defaultThemeName=cas-theme-demo
cas.viewResolver.basename=demo_views
...
```