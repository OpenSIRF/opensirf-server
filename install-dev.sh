#!/bin/bash

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
      config.ssh.insert_key = false
      config.vm.provider "virtualbox" do |v|
        config.vm.network "private_network", :type => 'dhcp', :name => 'vboxnet0', :adapter => 2
      end
    end
	EOF

  cat <<-EOF > sirf-dev/opensirf-swift/Vagrantfile
	Vagrant.configure("2") do |config|
			config.vm.box = "opensirf/opensirf-ident-swift"
			config.ssh.insert_key = false
			config.vm.provider "virtualbox" do |v|
				v.memory = 4096
				v.cpus = 4
				config.vm.network "private_network", :type => 'dhcp', :name => 'vboxnet0', :adapter => 2
			end
		end
	EOF

  cat <<-EOF > sirf-dev/opensirf-fs/Vagrantfile
		Vagrant.configure("2") do |config|
			config.vm.box = "opensirf/opensirf-base"
			config.ssh.insert_key = false
			config.vm.provider "virtualbox" do |v|
				config.vm.network "private_network", :type => 'dhcp', :name => 'vboxnet0', :adapter => 2
			end
		end
	EOF

}

provisionSirfServer() {
  oldDir=$(pwd)
  cd sirf-dev/opensirf-server
  vagrant up
  cd $oldDir
}

provisionSwiftServer() {
  oldDir=$(pwd)
  cd sirf-dev/opensirf-swift
  vagrant up
  cd $oldDir
}

provisionFsServer() {
  oldDir=$(pwd)
  cd sirf-dev/opensirf-fs
  vagrant up
  cd $oldDir
}

checkPreReqs
prepare
provisionSirfServer
provisionSwiftServer
provisionFsServer