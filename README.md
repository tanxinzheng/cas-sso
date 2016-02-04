# Single Sign On for Central Authentication Service (SSO CAS)

## 简介
欢迎访问CAS单点登录中心认证服务项目Demo演示，此Demo基于CAS的开源框架并集成几种主流客户端的Demo演示，旨在解决集成SSO中常用的场景分析及技术解决方案，如有问题欢迎大家随时提交issue。

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
- [ ] 集成NodeJs客户端演示
- [x] 集成Remember Me功能
- [x] [集成Memcache功能](./cas-server-demo/README.md)
- [ ] 集成登录验证码
- [ ] 自定义注册页面
- [ ] OpenID Login for QQ
- [ ] OpenID Login for Wechat
- [ ] OpenID Login for Weibo
- [ ] OpenID Login for Alipay
- [ ] OAuth2 for Server
- [ ] OAuth2 for Client

##

## 意见反馈
欢迎大家随时提交反馈意见及功能需求，或fork之后new pull request，本人会及时对问题进行回复，[请戳我提交issue。](https://github.com/tanxinzheng/cas-sso/issues/new)

## 异常情况测试汇总
- 客户端登录后，CAS服务宕机不影响客户端操作（客户端在宕机过程中不与CAS服务端交互情况），且宕机期间无法注销用户信息

## 参考文献
1.  CAS 官网：<http://www.jasig.org/cas>
2.  CAS Document API：<http://jasig.github.io/cas/4.0.x/index.html>
