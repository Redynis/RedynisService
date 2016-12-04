# RedynisService

This is a service wrapper on Redis that offers usage-based dynamic partitioning as part of the Redynis project

## Requirements
* Java (1.7+)
* Gradle (2+)
* Redis (3.0)
* Apache Tomcat (8+)  (or any other Java web server like JBOSS)

## Instructions

Build the service using
```
gradle build
```

Deploy the .war on the {$TOMCAT_HOME}/webapps/ directory (or any other Java web server like JBOSS)

Configure redis instances for the KV-layer and the metadata layer by following [this tutorial](https://redis.io/topics/config).
