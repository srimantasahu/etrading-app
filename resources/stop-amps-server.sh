#!/bin/bash

WORKSPACE_DIR="/home/hppy/workspace"
PID_FILE="$WORKSPACE_DIR/amps.pid"

if [[ -f "$PID_FILE" ]]; then
  PID=$(cat "$PID_FILE")
  echo "[INFO] Stopping AMPS (PID $PID)..."
  kill "$PID" && rm -f "$PID_FILE"
  echo "[INFO] Stopped AMPS server."
else
  echo "[WARN] No amps.pid found. Is AMPS running?"
fi
