Java webapp protected by the Spring Security CAS client (https://github.com/spring-projects/spring-security)
==

Maven demo using the CAS client from the Spring Security project (v3.2.5) to protect a web application.

Use **mvn clean compile jetty:run** to start the webapp on **http://localhost:8081**. The url 'protected/index.jsp' is protected and should trigger a CAS authentication.

Most of the configuration is defined in the **src/main/resources/securityContext.xml** file.

A specific logout application url is available at: http://localhost:8081/j_spring_security_logout.

Run your CAS server on http://localhost:8080/cas.
