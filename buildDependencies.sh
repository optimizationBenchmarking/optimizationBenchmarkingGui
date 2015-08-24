projectDir=`pwd`
echo "Current Directory: $projectDir"
cd "/tmp"
baseProject="/tmp/optimizationBenchmarking"
mkdir "$baseProject"
cd "$baseProject"
echo "Current dir: $baseProject"
wget https://codeload.github.com/optimizationBenchmarking/optimizationBenchmarking/tar.gz/master
tar -xvf "master"
cd "$baseProject/optimizationBenchmarking-master"
mvn -Dmaven.test.skip=true compile package install
cd "/tmp"
rm -rf "$baseProject"
cd "$projectDir"
