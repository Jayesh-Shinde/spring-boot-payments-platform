#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Build all maven projects
mvn clean install

# Build and push docker images
(cd account-services && docker build -t jayesh888/account-service:latest . && docker push jayesh888/account-service:latest)
(cd ledger-service && docker build -t jayesh888/ledger-service:latest . && docker push jayesh888/ledger-service:latest)
(cd transaction-service && docker build -t jayesh888/transaction-service:latest . && docker push jayesh888/transaction-service:latest)
(cd notification-service && docker build -t jayesh888/notification-service:latest . && docker push jayesh888/notification-service:latest)
(cd audit-service && docker build -t jayesh888/audit-service:latest . && docker push jayesh888/audit-service:latest)

echo "All services built and pushed successfully!"
