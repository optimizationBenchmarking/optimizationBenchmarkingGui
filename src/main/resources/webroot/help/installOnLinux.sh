#!/bin/bash

# upate list of known packages
sudo apt-get -q -y update

# install `texlive`
# `texlive` contains a LaTeX compiler and all the necessary
# tools required by our software to generate pdf files from
# LaTeX reports. Without a LaTeX installation, you can still
# create reports in the LaTeX format, but they will not be
# compiled to pdf.
if [ $(dpkg-query -W -f='${Status}' texlive 2>/dev/null | grep -c "ok installed") -eq 0 ];
then
  if [ $(dpkg-query -W -f='${Status}' texlive-full 2>/dev/null | grep -c "ok installed") -eq 0 ];
  then
    echo "Installing `texlive` so that LaTeX reports can be compiled to pdf."
    sudo apt-get -q -y install texlive
  fi
fi

# install `R`
# The `R` language and system provides several powerful and
# highly-efficient machine learning algorithms. Some of the
# modules of our system use these tools and therefore require
# (and, internally, start) `R`.
if [ $(dpkg-query -W -f='${Status}' r-base 2>/dev/null | grep -c "ok installed") -eq 0 ];
then
  echo "Installing `r-base` so that we can use sophisticated Machine Learning algorithms to analyze your experiments." 
  sudo apt-get -q -y install r-base
fi

if [ $(dpkg-query -W -f='${Status}' r-base-dev 2>/dev/null | grep -c "ok installed") -eq 0 ];
then
   echo "Installing `r-base-dev` so that we can use sophisticated Machine Learning algorithms to analyze your experiments." 
  sudo apt-get -q -y install r-base-dev
fi

# install additional R packages
sudo Rscript -e 'if(!(require("vegan"))) install.packages("vegan", repos="http://cran.us.r-project.org", dependencies=TRUE, clean=TRUE)'
sudo Rscript -e 'if(!(require("cluster"))) install.packages("cluster", repos="http://cran.us.r-project.org", dependencies=TRUE, clean=TRUE)'
sudo Rscript -e 'if(!(require("fpc"))) install.packages("fpc", repos="http://cran.us.r-project.org", dependencies=TRUE, clean=TRUE)'
sudo Rscript -e 'if(!(require("NbClust"))) install.packages("NbClust", repos="http://cran.us.r-project.org", dependencies=TRUE, clean=TRUE)'
sudo Rscript -e 'if(!(require("mclust"))) install.packages("mclust", repos="http://cran.us.r-project.org", dependencies=TRUE, clean=TRUE)'
sudo Rscript -e 'if(!(require("stats"))) install.packages("stats", repos="http://cran.us.r-project.org", dependencies=TRUE, clean=TRUE)'
sudo Rscript -e 'if(!(require("apcluster"))) install.packages("apcluster", repos="http://cran.us.r-project.org", dependencies=TRUE, clean=TRUE)'

# install `subversion`
# Subversion is a small tool for interacting with `svn`
# repositories. We need it to download examples.
if [ $(dpkg-query -W -f='${Status}' subversion 2>/dev/null | grep -c "ok installed") -eq 0 ];
then
  echo "Installing `subversion`, so that you can download examples from the web automatically." 
  sudo apt-get -q -y install subversion
fi