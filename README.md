# Welcome to PowerDale

PowerDale is a small town with around 100 residents. Most houses have a smart meter installed that can save and send
information about how much energy a house has consumed.

There are three major power suppliers in town that charge different amounts for the power they supply.

- _Dr Evil's Dark Energy_
- _The Green Eco_
- _Power for Everyone_

Each customer with a smart meter needs to sign up for one of the price plans offered by these suppliers.

# Introducing JOI Energy

JOI Energy is a new start-up in the energy industry. Rather than selling energy they want to differentiate themselves
from the market by recording their customers' energy usage from their smart meters and recommending the best supplier to
meet their needs.

This codebase models and implements the logic for storing and fetching energy consumptions reading from a smart meter, 
as well as the logic to recommend the cheapest price plan for a particular user's energy consumption patterns.

Unfortunately, as the codebase has evolved, it has gathered a number of code smells and some questionable design decisions.
Our goal with this exercise would be to deliver value by implementing a new feature, while at the same time improving the
codebase by refactoring away any code smells we identify.

## Users

To trial the new JOI software 5 people from the JOI accounts team have agreed to test the service and share their energy
data.

| User   | Smart Meter ID  | Power Supplier        |
|--------|-----------------|-----------------------|
| Sarah  | `smart-meter-0` | Dr Evil's Dark Energy |
| Paolo  | `smart-meter-1` | The Green Eco         |
| Chitra | `smart-meter-2` | Dr Evil's Dark Energy |
| Andrei | `smart-meter-3` | Power for Everyone    |
| Jingyi | `smart-meter-4` | The Green Eco         |

These values are used in the examples and tests.

## Requirements

The project requires [Java 21](https://adoptium.net/) or higher.

## Useful commands

### Build the project

Compile the project, run the tests and creates an executable JAR file

```console
$ ./gradlew build
```

### Run tests

There are two types of tests, the unit tests and the functional tests. These can be executed as follows.

- Run unit tests only

  ```console
  $ ./gradlew test
  ```

- Run functional tests only

  ```console
  $ ./gradlew functionalTest
  ```

- Run unit, functional and code style tests

  ```console
  $ ./gradlew check
  ```


## API
TODO: add some documentation for the classes

```
