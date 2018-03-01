# Distributed Systems Charly Mrazeck, Quentin Dell, Jordan Hiertz, Baptiste Oberbach

This git repository contains both the source code and the necessary library for the 3 exercices  

## Prerequisites

All the libraries contained in the folder lib should be added in the classpath  
- 

## Exercice 1

- Execute MultithreadedTCPServer.java wich launch the broker. 
- Execute TraderService in order to send data to the broker.  
NOTE : Even though TraderService will launch some traders which use activeMQ they will not be active since there are no news from the journalist.  
The traders will close if the server is closed.  

## Exercice 2

- Execute MultithreadedTCPServer.java wich launch the broker.
- Execute TraderService in order to have some data in the history.
- Execute PriceClient which will send request to the PriceService.       
The PriceService has 3 functions :   
 - Stocks will send all the pending asks. Data will be send in pack wich size can be parametered in the client in order to avoid  large transfert.
 - History will send all the completed transactions. Data will be send in pack wich size can be parametered in the client in order to avoid  large transfert.
 - LastPrice will send the price of the last transactions for each stock. A parameter can be added in order to request a particuliar stock.


## Exercice 3

- Execute MultithreadedTCPServer.java wich launch the broker.
- Execute PublisherService.java wich launch random good or bad news. 
- Execute TraderService in order to have some data in the history. Since the publisherService is active the improvedTrader will catch the news and act acording to them.
