projectDir=`pwd`
echo "Current Directory: $projectDir"
cd "/tmp"
baseProject="/tmp/optimizationBenchmarking"
mkdir "$baseProject"
cd "$baseProject"
echo "Current dir: $baseProject"
wget -nv -O "master.tar.gz" https://codeload.github.com/optimizationBenchmarking/optimizationBenchmarking/tar.gz/master
tar -xf "master.tar.gz"
cd "$baseProject/optimizationBenchmarking-master"
mvn -Dmaven.test.skip=true compile package install
cd "/tmp"
rm -rf "$baseProject"
cd "$projectDir"
