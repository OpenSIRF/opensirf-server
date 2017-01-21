#!/bin/bash

# Constants
args=$1
gitBranch="develop"
swiftBoxName="devsirfswift"
fsBoxName="devsirffs"
sirfBoxName="devsirfserver"
chefSetup="https://raw.githubusercontent.com/OpenSIRF/opensirf-server/$gitBranch/chef-setup.sh"

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
  if [ "$1" != "-n" ]; then
      mkdir -p sirf-dev
      mkdir -p sirf-dev/$sirfBoxName
      mkdir -p sirf-dev/$swiftBoxName
      mkdir -p sirf-dev/$fsBoxName

      cat <<-EOF > sirf-dev/$sirfBoxName/Vagrantfile
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

      cat <<-EOF > sirf-dev/$swiftBoxName/Vagrantfile
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

      cat <<-EOF > sirf-dev/$fsBoxName/Vagrantfile
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
  fi
}

# $1=box name
addHost() {
  pubIP=$(VBoxManage guestproperty get $1 "/VirtualBox/GuestInfo/Net/2/V4/IP" | awk '{print $2}')
  ip[$1]=$pubIP
}

# $1=box name
provisionServer() {
  oldDir=$(pwd)
  cd sirf-dev/$1
  if [ "$args" != "-n" ]; then
    vagrant up
  fi
  vmId[$1]=$(cat .vagrant/machines/default/virtualbox/index_uuid)
  addHost $1
  cd $oldDir
  runChefSetup $1
}

provisionSirfServer() {
  provisionServer $sirfBoxName
}

provisionSwiftServer() {
  provisionServer $swiftBoxName
}

provisionFsServer() {
  provisionServer $fsBoxName
}

runChefSetup() {
  vagrant ssh ${vmId[$1]} -c "sudo sh -c \"curl -s $chefSetup | bash\""
}

configureHosts() {
  for box in $sirfBoxName $swiftBoxName $fsBoxName; do
    for host in $sirfBoxName $swiftBoxName $fsBoxName; do
      vagrant ssh ${vmId[$box]} -c "sudo sh -c \"echo ${ip[$host]} $host >> /etc/hosts\""
    done
  done
}

declare -A ip
declare -A vmId

checkPreReqs
prepare
provisionSirfServer
provisionSwiftServer
provisionFsServer
configureHosts
