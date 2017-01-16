#!/bin/bash

for box in opensirf-server opensirf-swift opensirf-fs
do
	cd sirf-dev/$box
	vagrant destroy -f
	cd ../../
done

rm -fr sirf-dev/
