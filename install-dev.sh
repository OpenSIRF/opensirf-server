#!/bin/bash

# Constants
swiftBoxName="devsirfswift"
fsBoxName="devsirffs"
sirfBoxName="devsirfserver"

# $1=numErrors
errorExit() {
  echo
  echo "$1 errors have been found above. Please correct the errors and try again."
  exit 1
}

# $1=component
hasComponent() {
  which $1 > /dev/null 2>&1 || rc=$?
  if [ ! -z $rc ]; then
    echo "ERROR: $1 could not be found. Please install $1 and try again."
    return 1
  fi  
}

checkPreReqs() {
  hasComponent vagrant || ((errors++))
  hasComponent virtualbox || ((errors++))

  if [ ! -z $errors ]; then
    errorExit $errors
  fi
}

prepare() {
  mkdir sirf-dev
  mkdir sirf-dev/opensirf-server
  mkdir sirf-dev/opensirf-swift
  mkdir sirf-dev/opensirf-fs

  cat <<-EOF > sirf-dev/opensirf-server/Vagrantfile
    Vagrant.configure("2") do |config|
      config.vm.box = "opensirf/opensirf-server"
      config.vm.hostname = "$sirfBoxName"
      config.ssh.insert_key = false
      config.vm.provider "virtualbox" do |v|
	v.name = "$sirfBoxName"
        config.vm.network "public_network", :adapter => 3
        config.vm.network "private_network", :type => 'dhcp', :name => 'vboxnet0', :adapter => 2
      end
    end
	EOF

  cat <<-EOF > sirf-dev/opensirf-swift/Vagrantfile
	Vagrant.configure("2") do |config|
			config.vm.box = "opensirf/opensirf-ident-swift"
			config.vm.hostname = "$swiftBoxName"
			config.ssh.insert_key = false
			config.vm.provider "virtualbox" do |v|
				v.memory = 4096
				v.cpus = 4
				v.name = "$swiftBoxName"
				config.vm.network "private_network", :type => 'dhcp', :name => 'vboxnet0', :adapter => 2
				config.vm.network "public_network", :adapter => 3
			end
		end
	EOF

  cat <<-EOF > sirf-dev/opensirf-fs/Vagrantfile
		Vagrant.configure("2") do |config|
			config.vm.box = "opensirf/opensirf-base"
			config.vm.hostname = "$fsBoxName"
			config.ssh.insert_key = false
			config.vm.provider "virtualbox" do |v|
				v.name = "$fsBoxName"
				config.vm.network "public_network", :adapter => 3
				config.vm.network "private_network", :type => 'dhcp', :name => 'vboxnet0', :adapter => 2
			end
		end
	EOF

}

# $1=box name
resolveHost() {
  #boxId=$(cat .vagrant/machines/default/virtualbox/index_uuid)
  pubIP=$(VBoxManage guestproperty get $1 "/VirtualBox/GuestInfo/Net/2/V4/IP" | awk '{print $2}')
  echo "$pubIP $1" >> ../hosts.add
}

provisionSirfServer() {
  oldDir=$(pwd)
  cd sirf-dev/opensirf-server
  vagrant up
  resolveHost $sirfBoxName
  cd $oldDir
}

provisionSwiftServer() {
  oldDir=$(pwd)
  cd sirf-dev/opensirf-swift
  pwd
  ls *
  vagrant up
  resolveHost $swiftBoxName
  cd $oldDir
}

provisionFsServer() {
  oldDir=$(pwd)
  cd sirf-dev/opensirf-fs
  vagrant up
  resolveHost $fsBoxName
  cd $oldDir
}

checkPreReqs
prepare
provisionSirfServer
provisionSwiftServer
provisionFsServer
