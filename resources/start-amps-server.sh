#!/bin/bash

# Define the paths
AMPS_HOME="/home/hppy/workspace/inst/AMPS-5.3.4.129-Release-Linux"
CONF_PATH="/home/hppy/workspace/repo/etrading-app/resources"

CONFIG_FILE="$CONF_PATH/amps_config.xml"

# Check if the amps_config.xml file exists
if [ ! -f "$CONFIG_FILE" ]; then
  echo "amps_config.xml not found. Generating sample config..."
  $AMPS_HOME/bin/ampServer --sample-config > "$CONFIG_FILE"
else
  echo "amps_config.xml already exists. Skipping generation."
fi

# Go to home dir
cd /home/hppy/workspace

# Start the AMP Server with the config
$AMPS_HOME/bin/ampServer --config $CONFIG_FILE &

