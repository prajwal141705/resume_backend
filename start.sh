#!/bin/bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIB_PATH="/home/karan-m-gharale/.local/lib/libfakeuname.so"
DATA_DIR="/home/karan-m-gharale/.local/share/mongodb_data"

mkdir -p "$DATA_DIR" "/home/karan-m-gharale/.local/lib"

# Check if MongoDB is already running
if ! mongosh --eval "db.adminCommand('ping')" --quiet >/dev/null 2>&1; then
    echo "Starting local MongoDB server..."
    LD_PRELOAD="$LIB_PATH" mongod --dbpath "$DATA_DIR" --port 27017 --bind_ip 127.0.0.1 --logpath "$DATA_DIR/mongod.log" --fork
    echo "MongoDB started on port 27017."
else
    echo "MongoDB is already running on port 27017."
fi

echo "Starting Resume Builder Spring Boot Backend..."
mvn spring-boot:run
