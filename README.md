# TnBox: The Official Typhon VM

[ ![Download](https://api.bintray.com/packages/iconmaster5326/maven/tnbox/images/download.svg) ](https://bintray.com/iconmaster5326/maven/tnbox/_latestVersion)

## Overview

TnBox is a [Typhon](https://github.com/TyphonLang/Typhon) plugin that provides a virtual machine for running Typhon code in.

## Running

To run TnBox, first download the JAR from [Bintray](https://bintray.com/iconmaster5326/maven/tnbox/_latestVersion), our Maven repository and download provider. Note that there are three JARs to download per version; to get the one you can run from the command line, look for the one with `-complete` at the end of the file name. Then invoke it like so:

```
java -jar tnbox-complete.jar <your file>.tn
```

This will execute the Typhon file given in as an argument. Run it without arguments to see a list of all the options you can specify.

## Building

This project uses [Gradle](http://gradle.org) to build. There are two methods to build it:

### Dependencies from Maven

To download Typhon from Maven along with the other dependencies, run Gradle like so:

```
./gradlew -PfromMaven <tasks...>
```

This specifies a property that makes TnBox fetch Typhon from Maven.

### Dependencies from Local Files

To let TnBox use a Typhon JAR built locally, ensure this repository and Typhon's repository are sitting inside your local machine, in the same directory. Then run the following in the Typhon repository:

```
./gradlew jar
```

And then you can go back to TnBox's repository and run Gradle as you see fit. Run `gradle jar` after you make any changes to Typhon itself.

## Contributing

Feel free to make any pull requests you desire, and check out our [issue tracker](https://github.com/TyphonLang/Typhon/issues) to report any bugs.