# optimizationBenchmarkingGui

[<img alt="Semaphore Build Status" src="https://semaphoreci.com/api/v1/projects/188615a7-047a-4302-9233-13313b587066/519593/shields_badge.svg" height="20"/>](https://semaphoreci.com/thomasweise/optimizationbenchmarkinggui)

This is a graphical user interface (GUI) for the [optimizationBenchmarking Tool Suite](http://www.optimizationBenchmarking.org/). Please visit the main [website](http://www.optimizationBenchmarking.org/) for more information about that the tool suite does.

## Use Cases of the GUI

This GUI is developed as Java Server Page (JSP) in a stand-alone (fat) jar. If you use the
fat jar, no installation is required whatsoever, you can simply execute it at the command
line. There are two scenarios in which you may want to use the GUI:

1. Working locally on your own computer.
2. Running on a powerful server computer and accessed remotely from your own computer
   inside your own intranet.

The second use case is attractive if you deal with large sets of experimental results
and complex evaluations. Then it will allow you to conveniently start an evaluation
process consuming lots of memory and runtime, while you can still work on your PC.

**WARNING:** The current version of the GUI does neither contain user management nor
any security features. You must **never** make it accessible from the internet. Matter
of fact, you must **only** make it accessible to people working in your own group whom
you fully trust.

## Command Line Arguments

- `dontOpenBrowser`: By default, the GUI will first start a local web server and then
  open a web browser to view the main page. If you have the second use case, i.e., run
  the GUI server centrally, you may not want to open a web browser on your server and,
  hence, can specify this parameter.
  
- `rootPath`: The root data folder. Only files and directories within this folder can
  be accessed with the GUI. By default, the folder `data` in the path where you started
  the GUI.
  
- `port`: The port to use for the server. By default `8080`. Set to `0` for random port,
  which may make sense if `dontOpenBrowser` is not specified.

## System Requirements

1. Java 1.7 JDK
2. optional: a LaTeX installation such as MikTeX or TexLive
3. the [third party libraries](https://github.com/optimizationBenchmarking/optimizationBenchmarkingGui/blob/master/LICENSE.md) the GUI and the `optimizationBenchmarking` depend on if and only if you do not use the stand-alone/full executable