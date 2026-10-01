#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

JAR_FILE="target/server-web-manager-1.0.0.jar"

if ! command -v java >/dev/null 2>&1; then
    echo "Error: java tidak ditemukan. Pastikan JDK 17 atau lebih baru sudah terpasang."
    exit 1
fi

if [ ! -f "$JAR_FILE" ]; then
    bash build.sh
fi

java -jar "$JAR_FILE"