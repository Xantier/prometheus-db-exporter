# Prometheus Database query exporter

Simple Prometheus client application that exports database query results to an endpoint that Prometheus can pull data from.

Built with Spring Boot, Kotlin and Prometheus simple client. Externalized config to run queries based on YAML configuration files. 
  
Application has two config classes, first one, `App.kt`, creates Spring context and initializes base Spring boot monitoring. 
This context also reads all YAML files from queries folder. Second configuration, `QueryConfig.kt`, creates individual Prometheus export metric objects.

Set up database properties and query intervals in `application.properties` file within `config` folder. Note that queries are launched at the same time for each type of metric. 

Query file needs 5 properties:

1. `name` - Name of the metric 
2. `description` - Prometheus help text associated with the metric
3. `type` - metric type. Possible values:
  * `gauge`
  * `counter`
  * `histogram`
  * `summary` 
4. `labels` - a list of labels to be associated with the metric
5. `query` - The actual SQL query. Note that number of columns return should be `length of labels + 1`, where the final column is the actual metric number.

Example query file:
```
name: amount_of_users
description: Gauge to how many users are in the database
type: gauge
labels:
  - 'registration_type'
  - 'age_bracket'

query: |
  SELECT
  source as registration_type,
  age_bracket,
  count(*) as amount
  FROM USER
  GROUP BY source, age_bracket
  ORDER BY source, age_bracket

```

## Run
```
mvn spring-boot:run
```

Metrics are available at: 
```
localhost:8080/metrics
```

## Different databases
Add maven dependency for your DB vendor to pom.xml. Change DB drivers in application.properties.

## Distribute


1. Build
  * `mvn clean package`
2. Copy created JAR file (prometheus-db-exporter-1.0.0.jar) from /target to your desired location
3. Create 2 folders on that same directory:
  * `config`
  * `queries`

4. Fill mentioned folders with application.properties & query files.
5. Run with `java -jar prometheus-db-exporter-1.0.0.jar`


# Licence


[The MIT License](http://opensource.org/licenses/mit-license.php)
