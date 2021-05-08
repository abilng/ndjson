# ndjson

[![Build Status](https://travis-ci.org/abilng/ndjson.svg?branch=master)](https://travis-ci.org/abilng/ndjson)
[![Maven Central](https://img.shields.io/maven-central/v/in.abilng/ndjson.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22in.abilng%22%20AND%20a:%22ndjson%22)

Java library for creating and parsing [ND-JSON](http://ndjson.org/)

Uses [Jackson JSON parser](https://github.com/FasterXML/jackson)

## Usage

**Deserialization**

```java
    NdJsonObjectMapper ndJsonObjectMapper = new NdJsonObjectMapper();
    InputStream is = ....;
    Stream<Car> readValue = ndJsonObjectMapper.readValue(is, Car.class);
```

**Serialization**

```java
    NdJsonObjectMapper ndJsonObjectMapper = new NdJsonObjectMapper();
    OutputStream out = ... ;
    Stream <Car> carStream = ...;
    ndJsonObjectMapper.writeValue(out, carStream);
```

## How to add NDJSON to your project 

Maven:
```xml
<dependency>
  <groupId>in.abilng</groupId>
  <artifactId>ndjson</artifactId>
  <version>1.0.1</version>
</dependency>
```

SBT:
```sc
libraryDependencies += "in.abilng" % "ndjson"
```
