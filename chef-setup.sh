#!/bin/bash

cookbookLocation="https://github.com/OpenSIRF/opensirf_cookbook_register.git"

mkdir /var/lib/sirf/cookbooks
mkdir ~/cookbooks
cd /var/lib/sirf/cookbooks
git clone $cookbookLocation
cd opensirf_cookbook_register
berks vendor ~/cookbooks
