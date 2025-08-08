#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")"/.. && pwd)"
OUT_DIR="$PROJECT_ROOT/out"
SRC_DIR="$PROJECT_ROOT/src/main/java"

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

find "$SRC_DIR" -name "*.java" > "$OUT_DIR/sources.list"

javac -d "$OUT_DIR" @"$OUT_DIR/sources.list"

java -cp "$OUT_DIR" com.example.finance.Main