# Lab 3 — Spring DAO selection and the Open/Closed Principle

Java 11+, Maven 3.6+, Spring Context 5.3.30; JUnit 4 tests.
**No database, MySQL, Docker, SQL schema or API server is required.**
All four sources return the handout's simulated fixed values, including DaoFile and DaoApi.
No actual file-data parsing or network API call is implied.

## Run

```powershell
cd C:\Users\PC\Documents\study_5\JEE\labs2\lab3
mvn clean test
mvn compile exec:java
```

Default strategy: dynamic; packaged app.properties selects dao2 -> result 300.0.
In IntelliJ import pom.xml and run Main with JDK 11 or newer.

### Strategies and expected results

| Strategy | Maven option | Expected result |
|---|---|---:|
| Primary | "-Dexec.args=primary" | 200 |
| Configuration alias | "-Dexec.args=alias" | 440 |
| Dynamic properties | default | 300 (dao2) |
| Prod profile | "-Dexec.args=profiles prod" | 200 |
| Dev profile | "-Dexec.args=profiles dev" | 300 |
| List/Map aggregation | "-Dexec.args=multiple" | 650 |
| Expected ambiguity | "-Dexec.args=ambiguous" | reports NoUniqueBeanDefinitionException |

Example:

```powershell
mvn compile exec:java "-Dexec.args=primary"
mvn exec:java "-Dexec.args=alias"
mvn exec:java "-Ddao.target=daoFile"
mvn exec:java "-Ddao.target=daoApi"
mvn exec:java "-Dexec.args=profiles prod"
mvn exec:java "-Dexec.args=profiles dev"
mvn exec:java "-Dexec.args=multiple"
mvn exec:java "-Dexec.args=ambiguous"
```

In IntelliJ set Program arguments to primary, alias, profiles prod, profiles dev, multiple or ambiguous.
For dynamic selection, leave Program arguments empty and set VM options to -Ddao.target=daoApi.
The trace confirms the actual injected implementation.

## External properties without recompiling

Packaged src/main/resources/app.properties contains dao.target=dao2.
Copy dao-override.properties.example to dao-override.properties at the project root.
Edit its dao.target and rerun; MetierImpl remains unchanged.
To use another external file, supply -Ddao.config=C:/absolute/path/custom.properties.

Explicit selection precedence:
1. Java system property -Ddao.target.
2. Environment variable DAO_TARGET.
3. Spring property sources (external file overrides packaged app.properties).
4. Fallback dao2 if no property exists.

PowerShell environment example:

```powershell
$env:DAO_TARGET = "daoApi"
mvn compile exec:java
Remove-Item Env:DAO_TARGET
```

Package a runnable JAR:

```powershell
mvn package
java -Ddao.target=daoFile -jar target/spring-ocp-lab-1.0-SNAPSHOT.jar
```

System properties for java must precede -jar. DAO_TARGET also works with the JAR.

## OCP and corrections to the examples

- MetierImpl depends only on IDao, uses constructor injection, and has no selection qualifier,
  switch, concrete DAO instantiation or environment logic.
- @PostConstruct tracing uses javax.annotation-api, needed explicitly on Java 11+.
- Exactly one strategy is active per context; competing Primary selectors are not combined.
- @Primary is placed on the default configuration factory method so the same DaoImpl class
  remains reusable in every strategy.
- Alias/dynamic factories return an existing managed component rather than constructing duplicate
  source objects. These are primary selector bean definitions, not literal Spring name aliases.
- Dynamic selection looks up registered IDao beans, allowing new implementations without
  expanding a switch in the business code.
- Profiles are set BEFORE context refresh; setting them after construction would be too late.
- In multiple mode the single-DAO consumer is excluded. Only the four original sources are
  collected (100+150+180+220=650); selector aliases do not inflate the aggregation.
- The constructor parameter is named source: naming it dao can let Spring resolve by parameter
  name, masking the intended ambiguity demonstration.
- Ambiguous mode catches only the expected ambiguity and prints it; genuine configuration errors
  still fail. Its expected demonstration exits successfully.
- @Qualifier is not inherently an anti-pattern, but selection is kept outside this lab's business
  class to satisfy the stated OCP constraint.
- Plain Spring does not automatically load Spring Boot's application.properties conventions;
  the property sources and precedence are configured explicitly here.
- Selection occurs at startup. Changing properties requires a new context/application run;
  this is not hot swapping in an already-created singleton.

## Tests

Run OcpSelectionTest: ten tests cover all four sources, primary/alias strategies,
external-file override, profiles, aggregation, ambiguity, invalid target/options and business stubs.
Tests isolate system properties; an explicitly supplied process DAO_TARGET still has its documented
precedence above file configuration.

## Screenshots

Use Win + Shift + S and save real captures under screenshots/.
1. Project tree showing dao, metier, config, Main, app.properties, tests and pom.xml.
2. MetierImpl constructor and calcul: no implementation-specific selection.
3. Primary config and result 200, trace DaoImpl.
4. Alias config and result 440, trace DaoApi.
5. Dynamic selection: dao2=300, daoFile=360 and daoApi=440; show property or command.
6. Profile runs: prod=200 and dev=300.
7. Multiple extension: four bean names and result 650.
8. Ambiguous mode: expected NoUniqueBeanDefinitionException message.
9. Ten green tests / zero failures and BUILD SUCCESS.
10. Optional runnable JAR output showing environment or system-property override.

There are no database schema screenshots for this lab.
