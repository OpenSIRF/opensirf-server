#!/bin/bash
#
# OpenSIRF
# 
# Copyright IBM Corporation 2017.
# All Rights Reserved.
# 
# MIT License:
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
# 
# Except as contained in this notice, the name of a copyright holder shall not
# be used in advertising or otherwise to promote the sale, use or other
# dealings in this Software without prior written authorization of the
# copyright holder.

cookbookLocation="https://github.com/OpenSIRF/opensirf_cookbook_register.git"

# $1=node type; $2=recipe
resolveRunlist() {
  case $1 in
  "swift") 
    echo "-o recipe[opensirf_cookbook_register::$2]"
    ;;
  "fs")
    echo "-o recipe[opensirf_cookbook_register::$2]"
    ;;
  # No recipes for SIRF server
  esac
}

mkdir -p /var/lib/sirf/cookbooks
mkdir -p ~/cookbooks
cd /var/lib/sirf/cookbooks
if [ -d opensirf_cookbook_register ]; then
  rm -fr opensirf_cookbook_register.bak/
  mv opensirf_cookbook_register/ opensirf_cookbook_register.bak/
fi
git clone $cookbookLocation
cd opensirf_cookbook_register
berks vendor ~/cookbooks
cd

recipe=$1
nodeType=$(cat /var/lib/sirf/.server)
runListOptions=$(resolveRunlist $nodeType $recipe)

chef-client --local $runListOptions
