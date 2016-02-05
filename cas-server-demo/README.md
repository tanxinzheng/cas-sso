CAS Server 自定义扩展功能
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

## 扩展自定义属性

1. 新增MultipleAttributeUserDao类（类名可自定义）继承StubPersonAttributeDao，重写getPerson()方法

```
@Override
public IPersonAttributes getPerson(String uid) {
    //此处为方便测试，实际应取数据库数据
    Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
    attributes.put("userid", Collections.singletonList((Object)uid));
    attributes.put("username", Collections.singletonList((Object)"测试username"));
    attributes.put("password", Collections.singletonList((Object)"测试password"));//为保证安全性客户端不应返回密码
    attributes.put("email", Collections.singletonList((Object) "test@163.com"));
    return new AttributeNamedPersonImpl(attributes);
}
```
2. 修改deployerConfigContext.xml文件，注入MultipleAttributeDao
```
...
<!-- 使用自定义属性Dao       -->
<bean id="attributeRepository" class="com.github.tanxinzheng.cas.server.support.MultipleAttributeUserDao"/>

<!-- 注释原代码 -->
<!--<bean id="attributeRepository" class="org.jasig.services.persondir.support.StubPersonAttributeDao"-->
      <!--p:backingMap-ref="attrRepoBackingMap" />-->

<!--<util:map id="attrRepoBackingMap">-->
    <!--<entry key="uid" value="uid" />-->
    <!--<entry key="eduPersonAffiliation" value="eduPersonAffiliation" />-->
    <!--<entry key="groupMembership" value="groupMembership" />-->
<!--</util:map>-->
...
```
3. 修改casServiceValidationSuccess.jsp文件，此处作用为客户端校验获取用户信息的xml
```
<cas:authenticationSuccess>
...
// 添加代码段
<c:if test="${fn:length(assertion.chainedAuthentications[fn:length(assertion.chainedAuthentications)-1].principal.attributes) > 0}">
    <cas:attributes>
        <c:forEach var="attr" items="${assertion.chainedAuthentications[fn:length(assertion.chainedAuthentications)-1].principal.attributes}">
            <cas:${fn:escapeXml(attr.key)}>${fn:escapeXml(attr.value)}</cas:${fn:escapeXml(attr.key)}>
        </c:forEach>
    </cas:attributes>
</c:if>
...
</cas:authenticationSuccess>
```
4. 客户端获取用户扩展字段
- Spring Security
```
CasAssertionAuthenticationToken casAssertionAuthenticationToken = (CasAssertionAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
AttributePrincipal principal = casAssertionAuthenticationToken.getAssertion().getPrincipal();
Map attributes = principal.getAttributes();
String userid=(String)attributes.get("userid");
String username1 = (String)attributes.get("username");
String email = (String)attributes.get("email");
String username = authentication.getName();
```
- Apache Shiro
```
Subject subject = SecurityUtils.getSubject();
List<Object> listPrincipals = subject.getPrincipals().asList();
Map<String, String> attributes = (Map<String, String>) listPrincipals.get(1);
String userid=(String)attributes.get("userid");
String username1 = (String)attributes.get("username");
String email = (String)attributes.get("email");
```

### 异常情况

- CAS Client P3校验URL与CAS Server P3校验URL不一致会出现以下异常，P3校验URL：http://localhost:8080/cas/p3
```
java-spring-security-cas-client-demo  00:06:29.389 [qtp1869652507-23] ERROR org.jasig.cas.client.util.XmlUtils - The reference to entity "locale" must end with the ';' delimiter.
org.xml.sax.SAXParseException: The reference to entity "locale" must end with the ';' delimiter.
	at org.apache.xerces.util.ErrorHandlerWrapper.createSAXParseException(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.util.ErrorHandlerWrapper.fatalError(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.impl.XMLErrorReporter.reportError(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.impl.XMLErrorReporter.reportError(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.impl.XMLErrorReporter.reportError(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.impl.XMLScanner.reportFatalError(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.impl.XMLScanner.scanAttributeValue(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.impl.XMLNSDocumentScannerImpl.scanAttribute(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.impl.XMLNSDocumentScannerImpl.scanStartElement(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.impl.XMLDocumentFragmentScannerImpl$FragmentContentDispatcher.dispatch(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.impl.XMLDocumentFragmentScannerImpl.scanDocument(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.parsers.XML11Configuration.parse(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.parsers.XML11Configuration.parse(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.parsers.XMLParser.parse(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.parsers.AbstractSAXParser.parse(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.apache.xerces.jaxp.SAXParserImpl$JAXPSAXParser.parse(Unknown Source) ~[xercesImpl-2.10.0.jar:na]
	at org.jasig.cas.client.util.XmlUtils.getTextForElement(XmlUtils.java:159) ~[cas-client-core-3.3.3.jar:3.3.3]
	at org.jasig.cas.client.validation.Cas20ServiceTicketValidator.parseResponseFromServer(Cas20ServiceTicketValidator.java:87) [cas-client-core-3.3.3.jar:3.3.3]
	at org.jasig.cas.client.validation.AbstractUrlBasedTicketValidator.validate(AbstractUrlBasedTicketValidator.java:208) [cas-client-core-3.3.3.jar:3.3.3]
	at org.springframework.security.cas.authentication.CasAuthenticationProvider.authenticateNow(CasAuthenticationProvider.java:140) [spring-security-cas-3.2.5.RELEASE.jar:3.2.5.RELEASE]
	at org.springframework.security.cas.authentication.CasAuthenticationProvider.authenticate(CasAuthenticationProvider.java:126) [spring-security-cas-3.2.5.RELEASE.jar:3.2.5.RELEASE]
	at org.springframework.security.authentication.ProviderManager.authenticate(ProviderManager.java:156) [spring-security-core-3.2.5.RELEASE.jar:3.2.5.RELEASE]
	at org.springframework.security.cas.web.CasAuthenticationFilter.attemptAuthentication(CasAuthenticationFilter.java:242) [spring-security-cas-3.2.5.RELEASE.jar:3.2.5.RELEASE]
	at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:211) [spring-security-web-3.2.5.RELEASE.jar:3.2.5.RELEASE]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:342) [spring-security-web-3.2.5.RELEASE.jar:3.2.5.RELEASE]
	at org.jasig.cas.client.session.SingleSignOutFilter.doFilter(SingleSignOutFilter.java:100) [cas-client-core-3.3.3.jar:3.3.3]
```