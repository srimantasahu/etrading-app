#!/bin/bash

# Define the paths
AMPS_HOME="/home/hppy/workspace/inst/AMPS-5.3.4.129-Release-Linux"

# Go to home dir
cd /home/hppy/workspace

# Check if AMPS server is up and running
$AMPS_HOME/bin/spark ping -server localhost:9007 -type json

# Subscribe to a topic
$AMPS_HOME/bin/spark sow_and_subscribe -server localhost:9007 -topic quotereqs -type json

