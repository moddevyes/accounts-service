#!/bin/bash

# DATABASE
echo ""
echo "Creating ACCOUNTS database."
echo ""

docker run --name accounts_mysql_container -d -p 3301:3306 -e MYSQL_ROOT_PASSWORD_FILE=/run/secrets/mysql-root-password \
-e MYSQL_DATABASE=ecommerce_accounts_db \
-e MYSQL_USER=davidking \
-e MYSQL_PASSWORD=davidking!! \
--env-file config/.env.dev -v ./secrets:/run/secrets --network commerce-net --restart unless-stopped mysql:8.0.1

# DOCKER IMAGE
echo ""
echo "Building DOCKER image for accounts-service."
echo ""
docker build -t accounts-service .

echo ""
echo "Deploying/Running DOCKER image for accounts-service."
echo ""
docker run -d --env-file config/.env.dev --name=accounts_service_container --net=commerce-net -p 8001:8001 --restart unless-stopped accounts-service

# VERIFY
echo ""
echo "VERIFY deployment"
echo ""
docker ps
echo ""
echo ""
docker images
