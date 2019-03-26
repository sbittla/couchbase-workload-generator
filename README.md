# couchbase-workload-generator

# ReadMe

## Prerequisites
Set up a couchbase server by running the below docker command.

docker run -d --name db -p 8091-8096:8091-8096 -p 11210-11211:11210-11211 couchbase

### Create a user:
Open the server URL http://&lt;hostname&gt;:8091/ui/index.html and create a user<br/>
**Username:** Administrator<br/>
**Password:** admin@123

### Create a new cluster db1 with the appropriate cluster details

### Create a bucket - employee.

### Install JRE 8

### Install maven 3

## Generate Executable jar
Go to the project base folder where pom.xml file is located and run the below command to generate the executable jar <br/>
**mvn clean install**

Executable jar will be generated under the folder ./target/workloadgenerator-&lt;version&gt;-jar-with-dependencies.jar

## Generate Data and Run Workload

Copy ./target/workloadgenerator-&lt;version&gt;-jar-with-dependencies.jar and my_config.yml to the folder where you want to run the tests. 

Since ./target/workloadgenerator-&lt;version&gt;-jar-with-dependencies.jar is an executable jar, there is no need of external dependencies and the workload can run in Mac/linux/windows platform without errors. This executable jar is tested against Mac and Linux operating systems. 

### Command used for running the jar file from the project folder: 
java -jar ./target/workloadgenerator-&lt;version&gt;-jar-with-dependencies.jar &lt;file.yml&gt; &lt;populateData / runWorkload&gt; &lt;number of rows to populate&gt;

1.	Run below command to populate the data, this command populates about 1000 records into the couchbase database. <br/>
	**Ex 1:** java -jar ./target/workloadgenerator-&lt;version&gt;-jar-with-dependencies.jar my_config.yml populateData 1000

If you don’t give the third argument, by default it is going to populate 500 records. <br/>
	 **Ex2:** java -jar ./target/workloadgenerator-&lt;version&gt;-jar-with-dependencies.jar my_config.yml populateData

2.	Run below command to generate the workload. Running command 1 is pre-requisite for command 2.<br/>
	**Ex:** java -jar ./target/workloadgenerator-&lt;version&gt;-jar-with-dependencies.jar my_config.yml runworkload


## ./target/workloadgenerator-&lt;version&gt;-jar-with-dependencies.jar is tested with below configurations

### OSs Tested is:
macOS High Sierra 10.13.6 (17G65)
Darwin Kernel Version 17.7.0 (Open source operating system)


### Java versions tested is: 
Java(TM) SE Runtime Environment (build 1.8.0_192-b12), Java HotSpot(TM) 64-Bit Server VM (build 25.192-b12, mixed mode)
OpenJDK Runtime Environment (build 1.8.0_201-b09), OpenJDK 64-Bit Server VM (build 25.201-b09, mixed mode)

## Source Code

### Java files:
Configuration.java – is used for reading the content from &lt;filename&gt;.yml file.
QueryWorkloadGenerator.java – is used for populating the data and generating the workload.

### Data file:
my_config.yml – is used for keeping the run time data for workload generation.

**Note:** For populating the data, it uses only connection details. Rest of the employee related data is generated at runtime.

**Note:** Data validations are not covered in the current java programs.

**Note:** Unit test cases are not included.


Thank you!
