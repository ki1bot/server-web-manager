#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

if ! command -v javac >/dev/null 2>&1; then
    echo "Error: javac tidak ditemukan. Pastikan JDK 17 atau lebih baru sudah terpasang."
    exit 1
fi

if ! command -v jar >/dev/null 2>&1; then
    echo "Error: jar tidak ditemukan. Pastikan JDK 17 atau lebih baru sudah terpasang."
    exit 1
fi

rm -rf out target

mkdir -p out target

find src/main/java -type f -name "*.java" | sort > target/sources.txt

if [ ! -s target/sources.txt ]; then
    echo "Error: source Java tidak ditemukan."
    exit 1
fi

javac \
    --release 17 \
    -encoding UTF-8 \
    -d out \
    @target/sources.txt

jar \
    --create \
    --file target/server-web-manager-1.0.0.jar \
    --main-class com.rifqi.servermanager.App \
    -C out .

rm -f target/sources.txt

echo
echo "Build berhasil."
echo "JAR: target/server-web-manager-1.0.0.jar"