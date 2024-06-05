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

This codebase is for an API which their customers and smart meters will interact with.

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

These values are used in the code and in the following examples too.

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

### Run the application

Run the application which will be listening on port `8080`.

```console
$ ./gradlew bootRun
```

## API

Below is a list of API endpoints with their respective input and output. Please note that the application needs to be
running for the following endpoints to work. For more information about how to run the application, please refer
to [run the application](#run-the-application) section above.

### Endpoint 1: Store Readings

```text
POST /readings
```

Example of body

```json
{
  "smartMeterId": <smartMeterId>,
  "electricityReadings": [
    {
      "time": <time>,
      "reading": <reading>
    }
  ]
}
```

Parameters

| Parameter      | Description                                           |
|----------------|-------------------------------------------------------|
| `smartMeterId` | One of the smart meters' id listed above              |
| `time`         | The date/time (as epoch) when the _reading_ was taken |
| `reading`      | The consumption in `kW` at the _time_ of the reading  |

Example readings

| Date (`GMT`)      | Epoch timestamp | Reading (`kWh`) |
|-------------------|----------------:|----------------:|
| `2020-11-29 8:00` |      1606636800 |          0.0503 |
| `2020-11-29 9:00` |      1606636860 |          0.0621 |
| `2020-11-30 7:30` |      1606636920 |          0.0922 |
| `2020-11-31 8:30` |      1606636980 |          0.1223 |
| `2020-11-31 8:00` |      1606637040 |          0.1391 |

In the above example, the smart meter periodically sampled readings, in `kWh`, over multiple days. Note that since the smart meter is reporting the total consumption at that point in time, the reading value will either be higher or the same as the previous reading. These readings are then sent by the smart meter to the application using the provided REST API. 

The following POST request, is an example request using CURL, sends the readings shown in the table above.

```console
$ curl \
  -X POST \
  -H "Content-Type: application/json" \
  "http://localhost:8080/readings" \
  -d '{"smartMeterId":"smart-meter-0","electricityReadings":[{"time":1606636800,"reading":0.0503},{"time":1606636860,"reading":0.0621},{"time":1606636920,"reading":0.0222},{"time":1606636980,"reading":0.0423},{"time":1606637040,"reading":0.0191}]}'
```

The above command does not return anything.

### Endpoint 2: Get Stored Readings

```text
GET /readings/<smartMeterId>
```

Parameters

| Parameter      | Description                              |
|----------------|------------------------------------------|
| `smartMeterId` | One of the smart meters' id listed above |

Retrieving readings using CURL

```console
$ curl "http://localhost:8080/readings/read/smart-meter-0"
```

Example output

```json
[
  {
    "time": "2020-11-29T08:00:00Z",
    "reading": 0.0503
  },
  {
    "time": "2020-11-29T08:01:00Z",
    "reading": 0.0621
  },
  {
    "time": "2020-11-29T08:02:00Z",
    "reading": 0.0222
  },
  {
    "time": "2020-11-29T08:03:00Z",
    "reading": 0.0423
  },
  {
    "time": "2020-11-29T08:04:00Z",
    "reading": 0.0191
  }
]
```

### Endpoint 3: View Current Price Plan and Compare Usage Cost Against all Price Plans

```text
GET /price-plans/compare-all/<smartMeterId>
```

Parameters

| Parameter      | Description                              |
|----------------|------------------------------------------|
| `smartMeterId` | One of the smart meters' id listed above |

Sample call with CURL

```console
$ curl "http://localhost:8080/price-plans/compare-all/smart-meter-0"
```

Example output

```json
{
  "pricePlanComparisons": {
    "price-plan-2": 0.0002,
    "price-plan-1": 0.0004,
    "price-plan-0": 0.002
  },
  "pricePlanId": "price-plan-0"
}
```

### Endpoint 4: View Recommended Price Plans for Usage

```text
GET /price-plans/recommend/<smartMeterId>[?limit=<limit>]
```

Parameters

| Parameter      | Description                                          |
|----------------|------------------------------------------------------|
| `smartMeterId` | One of the smart meters' id listed above             |
| `limit`        | (Optional) limit the number of plans to be displayed |

Sample call with CURL

```console
$ curl "http://localhost:8080/price-plans/recommend/smart-meter-0?limit=2"
```

Example output

```json
[
  {
    "price-plan-2": 0.0002
  },
  {
    "price-plan-1": 0.0004
  }
]
```
