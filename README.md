# RedynisService

This is a service wrapper on Redis that offers usage-based dynamic partitioning as part of the Redynis project

## Requirements
* Java (1.7+)
* Gradle (2+)
* Redis (3.0)
* Apache Tomcat (8+) 

## Supported end-points

### Fetch
```
curl -X GET "http://<host>:<port>/RedynisService/redis?key=<key_string>"
```

### Store
```
curl -X POST "http://<host>:<port>/RedynisService/redis?key=<key_string>&value=<value_string>"
```
