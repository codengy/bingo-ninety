# Ticket Generator Challenge

A small challenge that involves building a [Bingo 90](https://en.wikipedia.org/wiki/Bingo_(United_Kingdom)) ticket generator.

**Requirements:**

* Generate a strip of 6 tickets
  - Tickets are created as strips of 6, because this allows every number from 1 to 90 to appear across all 6 tickets. If they buy a full strip of six it means that players are guaranteed to mark off a number every time a number is called.
* A bingo ticket consists of 9 columns and 3 rows.
* Each ticket row contains five numbers and four blank spaces
* Each ticket column consists of one, two or three numbers and never three blanks.
  - The first column contains numbers from 1 to 9 (only nine),
  - The second column numbers from 10 to 19 (ten), the third, 20 to 29 and so on up until
  - The last column, which contains numbers from 80 to 90 (eleven).
* Numbers in the ticket columns are ordered from top to bottom (ASC).
* There can be **no duplicate** numbers between 1 and 90 **in the strip** (since you generate 6 tickets with 15 numbers each)

**Please make sure you add unit tests to verify the above conditions and an output to view the strips generated (command line is ok).**

Try to also think about the performance aspects of your solution. How long does it take to generate 10k strips? 
The recommended time is less than 1s (with a lightweight random implementation)

# Bingo 90 Solution

The algorithm works in 5 phases:
* phase 1: generates list of shuffled numbers by columns
    - an example of shuffled numbers for one stripe  
        [ 11, 17, 16, 12, 19, 14, 18, 15, 13, 10 ]
* phase 2: generates numbers usage by columns
    - an example of numbers usage by columns for one stripe  
        [ 2, 1, 2, 1, 2, 1, 2, 1, 3 ]
* phase 3: generates mask for each stripe of ticket
    - an example of mask for one stripe  
        [ 0, 1, 1, 0, 1, 0, 1, 0, 1 ]
        [ 1, 0, 1, 1, 0, 0, 0, 1, 1 ]
        [ 1, 0, 0, 0, 1, 1, 1, 0, 1 ]
* phase 4: populates stripe lists (ticket) with numbers using lists from *shuffled numbers (phase 1)* and *numbers usage (phase 2)*
    - an example of numbers of stripe  
        [ 8, 12, 18, 21, 26, 32, 35, 49, 53, 56, 65, 76, 79, 80, 85 ]
* phase 5: populates final stripe lists (ticket) using *generated mask (phase 2)* and *stripe numbers (phase 4)*
    - an exampleof populated stripe  
        [ 8, 12, 21, 0, 49, 0, 0, 76, 0 ]  
        [ 0, 18, 0, 32, 0, 53, 65, 0, 80 ]  
        [ 0, 0, 26, 35, 0, 56, 0, 79, 85 ]  

# Prerequisites
* JVM 8
* Maven
# How to run
From the project root run command:
```
mvn clean package  
java -jar target/bingo-ninety-0.0.1-SNAPSHOT-jar-with-dependencies.jar -t 10 -s 6 -p
```
where parameters are:
* -t : number of tickets (required)
* -s : number of stripes (required)
* -p : print tickets in console (optional)

### Run Tests Only
From the project root run:
```
mvn clean test
```