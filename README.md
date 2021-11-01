# NicciShop

## Introduction

This project implements a service for merchants with customer loyalty programs.

The merchant’s web shop calls this RESTful API to reward customers; and when customers are spending their points. There are calls available for the merchant to query various information about the resulting transactions.

## Assumptions

- There is only one Merchant.

- Authentication has already happened and Authorisation is implicit since here there is only one Merchant.

- Merchant's web shop validates and approves requests from the Shopper before sending any requests to the Service.

- This system is used for only transactions with the loyalty points.

## Participants

### Service

The RESTful API providing a service to implement customer loyalty programs.

### Merchant

Who uses the Service to reward its customers in the loyalty program and operates the web shop where the loyalty points can be spent.

### Shopper

The customer of the Merchant who participates in the customer loyalty program.

## Use Cases

### 1. Shopper Submits an Order

The Merchant’s web shop submits a new request to the Service with the Shopper and transaction details, the Service creates a new transaction for the Shopper.

### 2. Merchant Queries a list of Shopper Transactions

The Merchant’s web shop submits a request to the Service, the Service returns all the transactions for the Shopper filtered by date.

### 3. Shopper Queries Account Status

The Merchant’s web shop submits a request to the Service, the Service responds with current balance and transactions filtered by date. The Merchant’s web shop displays information to the Shopper.

### 4. Shopper Requests a Refund

The Merchant’s web shop submits a change transaction request to the Service, the Service updates the transaction status and balance; and responds with the updated transaction.

## Implementation Status

For now only Use Case 1 and 2 are implemented.

## Swagger UI to Try the Service

http://localhost:8080/swagger-ui/#/