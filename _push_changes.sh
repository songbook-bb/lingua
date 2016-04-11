#!/bin/bash
#CATALOGUE=${1-/mnt/sda2/Users/qn/git/lingmem}
CATALOGUE=${1-/mnt/sda2/Eclipse/worksvn/october}
echo "Destination is $CATALOGUE"
if [ -z "$CATALOGUE" ]; then
    echo "Missing destination catalogue parameter!"
    exit 1
fi

if [ -d "$CATALOGUE" ]; then
  cp -v -f *.naq "$CATALOGUE"
else
 echo "Sorry - nothing is copied, $CATALOGUE is not a directory!"
fi

