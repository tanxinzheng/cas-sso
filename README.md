# Single Sign On for Central Authentication Service (SSO CAS)

## 简介
欢迎访问CAS单点登录中心认证服务项目Demo演示。

#### CAS提供企业级单点登录服务:
- 提供开放且完善的文档API
- 提供多种开源Java服务组件（Oauth2,OpenID,SAML,LDAP,etc...）
- 提供多种开发语言的客户端 Java, .Net, PHP, Perl, Apache, uPortal, and others
- 集成 uPortal, BlueSocket, TikiWiki, Mule, Liferay, Moodle and others
- 提供公共文档和实施技术支持
- 拥有广泛的用户群体

## CAS Demo功能
- [x] CAS服务端演示
- [x] 集成Spring Security客户端演示
- [x] 集成Apache Shiro客户端演示
- [x] 集成Remember Me功能
- [ ] 集成NodeJs客户端演示(待开发)
- [ ] 自定义注册页面
- [ ] OpenID Login for QQ
- [ ] OpenID Login for Wechat
- [ ] OpenID Login for Weibo
- [ ] OpenID Login for Alipay
- [ ] OAuth2 for Server
- [ ] OAuth2 for Client

## 异常情况测试情况
- 客户端登录后，CAS服务宕机不影响客户端操作（客户端在宕机过程中不与CAS服务端交互情况），且宕机期间无法注销用户信息

## 参考文献
1.  CAS 官网：<http://www.jasig.org/cas>
2.  CAS API：<http://jasig.github.io/cas/4.0.x/installation/
