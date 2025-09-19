#!/bin/bash

# Define the paths
export AMPS_HOME="/home/hppy/workspace/inst/AMPS-5.3.4.129-Release-Linux"
WORKSPACE_DIR="/home/hppy/workspace"

# Go to home dir
cd "$WORKSPACE_DIR" || exit 1

# Check if AMPS server is up and running
$AMPS_HOME/bin/spark ping -server localhost:9007 -type json

# Subscribe to a topic
$AMPS_HOME/bin/spark subscribe -server localhost:9007 -topic quotereqs-in -type nvfix

# Subscribe to a SOW topic
$AMPS_HOME/bin/spark sow_and_subscribe -server localhost:9007 -topic quotereqs -type json

