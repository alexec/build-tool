# BT is a Build Tool

With the aims to fix all the problems with Maven Gradle :)

Aims:

1. **reliable**
2. **fast** 

Principles:

1. Convention over configuration.
2. Work avoidance.

## How is this tool different?
### (a) Event Based With Concurrent Tasks

BT builds by having a set of tasks that are triggered by events. The initial event in `SourceCodeFound` and can trigger other tasks such as `CopyResources` which result in  a `ResourcesCopied` event.

Events end up on a queue and are dispatched as quickly as possible. This means that, unlike Maven or Gradle, tasks as well as projects can run concurrently.

### (b) Decomposed Configuration

Configuration is defined in the format of the tool. Projects are defined by `project.json` 

~~~json
{
  "artifact": "bt:bt:1.0.15-SNAPSHOT",
  "modules": [
    "src/api",
    "src/main",
    "src/junit4-support",
    "src/test"
  ]
}
~~~

Projects are composed of modules defined by `module.json`:

~~~json
{
  "dependencies": [
    ":api",
    "org.slf4j:slf4j-api:1.7.25",
    "ch.qos.logback:logback-classic:1.2.3",
    "com.google.googlejavaformat:google-java-format:1.6",
    "com.puppycrawl.tools:checkstyle:8.12",
    "com.fasterxml.jackson.core:jackson-databind:2.9.4",
    "javax.inject:javax.inject:1"
  ]
}
~~~

Dependencies are defined in `dependencies.json`, this is a bit rubbish and needs to be improved:

~~~json
{
  "org.slf4j:slf4j-api:1.7.25": [],
  "ch.qos.logback:logback-classic:1.2.3": [
    "ch.qos.logback:logback-core:1.2.3"
  ],
  "ch.qos.logback:logback-core:1.2.3": [],
  "com.google.googlejavaformat:google-java-format:1.6": [
    "com.google.guava:guava:22.0",
    "com.google.errorprone:javac-shaded:9+181-r4173-1"
  ],
  "com.google.guava:guava:22.0": [
    "com.google.code.findbugs:jsr305:1.3.9",
    "com.google.errorprone:error_prone_annotations:2.0.18",
    "com.google.j2objc:j2objc-annotations:1.1",
    "org.codehaus.mojo:animal-sniffer-annotations:1.14"
  ],
  "com.google.errorprone:javac-shaded:9+181-r4173-1": [],
  "com.puppycrawl.tools:checkstyle:8.12": [
    "antlr:antlr:2.7.7",
    "org.antlr:antlr4-runtime:4.7.1",
    "commons-beanutils:commons-beanutils:1.9.3",
    "commons-cli:commons-cli:1.4",
    "net.sf.saxon:Saxon-HE:9.8.0-12"
  ],
  "antlr:antlr:2.7.7": [],
  "org.antlr:antlr4-runtime:4.7.1": [],
  "commons-beanutils:commons-beanutils:1.9.3": [
    "commons-logging:commons-logging:1.2",
    "commons-collections:commons-collections:3.2.2"
  ],
  "commons-logging:commons-logging:1.2": [],
  "commons-collections:commons-collections:3.2.2": [],
  "commons-cli:commons-cli:1.4": [],
  "net.sf.saxon:Saxon-HE:9.8.0-12": [],
  "com.fasterxml.jackson.core:jackson-databind:2.9.4": [
    "com.fasterxml.jackson.core:jackson-annotations:2.9.0",
    "com.fasterxml.jackson.core:jackson-core:2.9.4"
  ],
  "com.fasterxml.jackson.core:jackson-annotations:2.9.0": [],
  "com.fasterxml.jackson.core:jackson-core:2.9.4": [],
  "javax.inject:javax.inject:1": [],
  "junit:junit:4.12": [
    "org.hamcrest:hamcrest-core:1.3"
  ],
  "org.hamcrest:hamcrest-core:1.3": []
}
~~~

Modules are named after the directory they are found in and can only the same group ID as the project.

Otherwise, there is no special configuration. For example:

* Want to use check-style? Include your `checkstyle.xml` file in the module root.
* Want to have a main-class? Include `resources/MANIFEST.MF` in your source code tree.

Oh yeah - no XML by default.

### (c) Opinionated

All code is passed via checkstyle and automatically formatted. 

### (d) Tests Are Just Modules

Maven and Gradle treat tests as part of a module. BT treats does not have separate test sources. Tests are just modules that define the tests as classes in `META-INF/annotations/org.junit.Test` (oh yeah - only JUnit support and only version 4).

### (e) Logging 

Logs of the build are dumped into `target/build.log`, not the console.

### (f) Not Such Thing As A Clean Build

Nope. If you want a clean build:

~~~bash
find . -name target | xargs -Rf 
~~~

## How can I write a task for BT?

Implement the `bt.api.Task` interface and add yourself to `META-INF/services/bt.api.Task`. To recieve an event, create a method annotated with `@Subscribe` taking a single argument, the event type.

SLF4J is used for logging. Log at debug level to write to `build.log`.