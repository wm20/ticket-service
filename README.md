# Project Title

Ticketing Service

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

Command-line Instructions
-------------------------

* **Prerequisites:**
    * Install the 1.8 version of [Java](https://java.com) and [Maven](https://maven.apache.org/download.html).
    * You may need to set your `JAVA_HOME`.
1. Unzip the file containing the application source
2. cd to the directory
3. In order to change the configuration, see file <unzip-dir>/src/main/resources/app.properties. Changing the hold time will affect the duration of unit tests.
4. Build and run tests using 'mvn test' (Note that there are concurrent tests that wait for hold time to expire and threads to cleanup, so these tests take a few minutes to run.)
5. To build package: 'mvn package'. This creates 'target/ticket-service-1.0.0-RELEASE-jar-with-dependencies.jar'
6. To Run the interactive CLI:  {JAVA_HOME}/bin/java -jar target/ticket-service-1.0.0-RELEASE-jar-with-dependencies.jar
7. To generate javadoc: 'mvn javadoc:jar'. 
8. The <unzip-dir>/doc directory has a class diagram as well.

```bash
$ cd <unzip-dir>/ticketing-service/
# build and package
mvn package

Command line run:
${JAVA_HOME}/bin/java -jar target/ticket-service-1.0.0-RELEASE-jar-with-dependencies.jar
```

To enable logging, please take a look at src/main/resources/log4j2.xml. The logs are in ticket-service.log.



## Running the tests

$ cd <unzip-dir>
$ mvn test # Note that there are concurrent tests that wait for hold time to expire and threads to cleanup, so these tests take a few minutes to run.

### Tests

See junit tests under src/test/java/TicketServiceImplTest

Example tests: 
1. Test number of seats available matches configured.
2. Test expired seat holds cleanup after configured time + overhead
3. Test concurrent client requests to hold (e.g. 50 threads requesting 100 seats each).
4. Test find and hold seats
5. Test reserve seats
6. Test that multiple holds with the same emailid (i.e, same customer) are denied. Only one active hold is permitted.
7. Test requesting holds greater than available seats is denied.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Versioning
Release 1.0.0


## Author

* **WM**

## Assumptions
1. As per specifications, no additional interfaces to query reservation, release/expire reservations
are added. This implies that once all seats have been reserved, the venue is full.  
2. Given that the interface specifies int type as the unique id for the seatHold, the id is cycled starting back from 1 after Integer.MAX_VALUE is reached.
3. For testing purposes, the system has been tested to 100k seats with the out of the box configuration (see app.properties) and the included unit tests.
4. SeatHold objects are used for both DTO and domain, but can be separated as an ideal design goal.
5. Any reservations require valid and current/unexpired seat hold, and must be placed within the 
configured max hold time after the seat hold is received.  
5. Seat ranking has a basic implementation by seat position, but can be extended to be more complex by factors such as rows, sections etc.
6. Customer is allowed to change email Id between hold and reservation (it's sufficient to provide the seatid)
7. The application is implemented as a standalone java application.
6. The implementation is Java as per spec. Version is 1.8
### Command line interface

The application has a CLI that can be used to interact with the service shown below
${JAVA_HOME}/bin/java -jar target/ticket-service-1.0.0-RELEASE-jar-with-dependencies.jar
```
======================================================================
Welcome to the Ticket Service CLI
======================================================================
Enter your selection number:
                 1. Print number of available seats
                 2. Find and hold seats
                 3. Reserve seats
                 0: Exit the application
1

         ***** Response: Number of seats available: 100000


Enter your selection number:
                 1. Print number of available seats
                 2. Find and hold seats
                 3. Reserve seats
                 0: Exit the application
2
                        Enter number of seats to find and hold:
15
                        Enter customer emailId:
a.b@c.net

        *****Response: Seat hold request: seatHoldId: 1, number of held seats: 15, error message: null


Enter your selection number:
                 1. Print number of available seats
                 2. Find and hold seats
                 3. Reserve seats
                 0: Exit the application
3
                        Enter Seat hold id for reservation:
1
                        Enter customer emailId:
a.b@c.net

        Response: Reserve seat request: d35d2f03-6c0f-4fb9-9d1e-a3056c416f45
Enter your selection number:
                 1. Print number of available seats
                 2. Find and hold seats
                 3. Reserve seats
                 0: Exit the application
0
User input was 0, system exiting
```




