#!/bin/bash
# script fails on error, unset vars, or pipe failures
set -euo pipefail

# Paths
AMPS_HOME="${AMPS_HOME:-/home/hppy/workspace/inst/AMPS-5.3.4.129-Release-Linux}"
CONF_PATH="${CONF_PATH:-/home/hppy/workspace/repo/etrading-app/resources}"
CONFIG_FILE="$CONF_PATH/amps_config.xml"
WORKSPACE_DIR="/home/hppy/workspace"
LOG_FILE="$WORKSPACE_DIR/amps.log"

# Ensure directories exist
mkdir -p "$CONF_PATH" "$WORKSPACE_DIR"

# Check for amps_config.xml
if [[ ! -f "$CONFIG_FILE" ]]; then
  echo "[INFO] amps_config.xml not found. Generating sample config..."
  "$AMPS_HOME/bin/ampServer" --sample-config > "$CONFIG_FILE"
else
  echo "[INFO] Using existing amps_config.xml at $CONFIG_FILE"
fi

# Move to workspace
cd "$WORKSPACE_DIR" || exit 1

# Start AMPS server in background with logging
echo "[INFO] Starting AMPS..."
nohup "$AMPS_HOME/bin/ampServer" --config "$CONFIG_FILE" > "$LOG_FILE" 2>&1 &

AMPS_PID=$!
echo "[INFO] AMPS started with PID $AMPS_PID, logging to $LOG_FILE"

# Optional: save PID to file for stop scripts
echo $AMPS_PID > "$WORKSPACE_DIR/amps.pid"
