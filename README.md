# Welcome to PowerDale

PowerDale is a small town with around 100 residents. Most houses have a smart meter installed that can save and send
information about how much energy a house has consumed. Each household needs to sign up with one of the available power 
suppliers, and choose a pricing plan offered by them. The household energy consumption is then charged based on the 
selected pricing plan.

# Introducing JOI Energy

JOI Energy is a new start-up in the energy industry. Rather than selling energy, they want to differentiate themselves
from the market by recording their customers' energy usage from their smart meters and recommending the best supplier to
meet their needs.

This codebase currently implements the logic for storing and fetching energy consumption readings from a smart meter, 
as well as the logic to recommend the cheapest price plan for a particular household's energy consumption patterns.

>Unfortunately, as the codebase has evolved, it has gathered tech debt in the form of a number of code smells and some 
questionable design decisions. Our goal for the upcoming exercise would be to deliver value by implementing a new 
feature using _Test Driven Development_ (TDD), while refactoring away the code smells we see. 
> 
>In preparation for this, please take some time to go through the code and identify any improvements, big or small, 
that would improve its maintainability, testability, and design.

## Initial Rollout

There are three major power suppliers in town, each with their own pricing plans:
- _Dr Evil's Dark Energy_
- _The Green Eco_
- _Power for Everyone_

To trial the new JOI software 5 residents have agreed to test the service and share their energy data.

| User   | Smart Meter ID  | Power Supplier        |
|--------|-----------------|-----------------------|
| Chitra | `smart-meter-0` | Dr Evil's Dark Energy |
| Paolo  | `smart-meter-1` | The Green Eco         |
| Sarah  | `smart-meter-2` | Dr Evil's Dark Energy |
| Andrei | `smart-meter-3` | Power for Everyone    |
| Jingyi | `smart-meter-4` | The Green Eco         |

These values are used in the examples and tests.

## Requirements

The project requires [Java 21](https://adoptium.net/) or higher.

## Useful commands

Compile the project, run the tests and creates an executable JAR file

```console
$ ./gradlew build
```

### Run tests

```console
$ ./gradlew test
```

### Sample run
We provide a console application to manually test the implemented workflows with a set of sample data. 
You can run it with the following command:
```console
$ ./gradlew run
```

## API Documentation

The codebase contains two service classes, _MeterReadingManager_ and _PricePlanComparator_, that serve as entry points to
the implemented features.

### MeterReadingManager
Provides methods to store and fetch the energy consumption readings from a given Smart Meter

> #### _public void_ storeReadings(_String smartMeterId, List<ElectricityReading> electricityReadings_)
Stores the provided _ElectricityReading_ collection in the indicated _SmartMeter_. If no 
_SmartMeter_ with a matching ID is found, then a new one will be created.

| Parameter             | Description                                                                 |
|-----------------------|-----------------------------------------------------------------------------|
| `smartMeterId`        | a valid smart meter id                                                      |
| `electricityReadings` | a collection of _ElectricityReading_ entries for the referenced smart meter |

An _ElectricityReading_ record consists of the following fields:

| Parameter | Description                                                   |
|-----------|---------------------------------------------------------------|
| `time`    | The date/time instant (as epoch) when the _reading_ was taken |
| `reading` | The total energy consumed so far, in kilowatt-hours (kWh)     |

Example readings

| Date (`GMT`)      | Epoch timestamp |   Reading (kWh) |
|-------------------|----------------:|----------------:|
| `2020-11-29 8:00` |      1606636800 |          0.0503 |
| `2020-11-29 9:00` |      1606636860 |          0.0621 |
| `2020-11-30 7:30` |      1606636920 |          0.0922 |
| `2020-11-31 8:30` |      1606636980 |          0.1223 |
| `2020-11-31 8:00` |      1606637040 |          0.1391 |

Thee above table shows some readings sampled by a smart meter over multiple days. Note that since the smart 
meter is reporting the total energy consumed up to that point in time, a reading's value will always be higher or the same as 
the reading from a previous point in time.

This method throws an exception if the provided _smartMeterId_ or _electricityReadings_ values are empty.

> #### _public List&lt;ElectricityReading>_ readReadings(_String smartMeterId_)
Returns an unsorted collection of _ElectricityReading_ values stored in the given _SmartMeter_.

| Parameter             | Description                                                                 |
|-----------------------|-----------------------------------------------------------------------------|
| `smartMeterId`        | a valid smart meter id                                                      |

This method throws an exception if the Smart Meter corresponding to the provided _smartMeterId_ cannot be found.


### PricePlanComparator
Provides price plan recommendations

> public List&lt;Map.Entry<String, BigDecimal>> recommendCheapestPricePlans(String smartMeterId, Integer limit)

Returns a list of the available price plans mapped to their corresponding energy consumption cost, calculated 
based on the energy consumption readings from the provided Smart Meter. This list is limit to the number of items 
provided by the 'limit' parameter.

| Parameter      | Description                             |
|----------------|-----------------------------------------|
| `smartMeterId` | a valid smart meter id                  |
| `limit`        | the maximum number of results to return |

This method throws an exception if the _SmartMeter_ corresponding to the provided _smartMeterId_ cannot be found.


---