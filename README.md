# Apache Spark on Alibaba Cloud Maxcompute

<p align="center">
<img src="https://github.com/apache/spark/blob/master/docs/img/spark-logo-hd.png" width="60%" alt="Apache Spark" />
</p>

Spark on MaxCompute is a computing service provided by MaxCompute. It is compatible with the open-source Spark. It provides a Spark computing framework based on unified computing resources and a dataset permission system, which allows you to submit and run Spark jobs in your preferred development method. Spark on MaxCompute can fulfill the diverse needs of data processing and analysis.<sup>1</sup>

In this repo, some common Spark operations is implemented, such as RDD, DataFrame, SparkSQL and MlLib, as well as some Maxcompute-specific operations such as ![ODPS](https://www.alibabacloud.com/product/maxcompute) (big data platform - not unlike bigquery or redshift), ![OSS](https://www.alibabacloud.com/product/oss) (object storage), and ![Dataworks](https://www.alibabacloud.com/product/ide) (unified orchestrator - airflow, if you may) 

## Local Environment Setup

To develop Spark on Maxcompute projects, we need to setup local development environment. The easiest way to do this is via ![Maxcompute Studio](https://plugins.jetbrains.com/plugin/9193-maxcompute-studio), an Intellij IDEA plugin. However, those without IntelliJ IDEA can still setup their own development environment.

#### 1. Requirements

* ![Spark on Maxcompute Client](http://odps-repo.oss-cn-hangzhou.aliyuncs.com/spark/2.3.0-odps0.32.2/spark-2.3.0-odps0.32.2.tar.gz?spm=a2c63.p38356.879954.7.40c173fbZZW5Iw&file=spark-2.3.0-odps0.32.2.tar.gz), extract it and remember the extracted path
* Java 1.8
* Maven

#### 2. Set environment variables

MacOS or Linux users can add the following variables in `~/.bash_profile` file.

````shell script
export SPARK_HOME=/path/to/extracted/spark/client/from/step/1/above
export PATH=$SPARK_HOME/bin:$PATH
````

make sure to run `source ~/.bash_profile` from your terminal to load the environment variables.

#### 3. Configure the spark-defaults.conf file

Go to `$SPARK_HOME/conf` directory. In there, a `spark-defaults.conf.template` file can be found. Copy this file and rename it to `spark-defaults.conf`. Open the .conf file to setup our Maxcompute configuration.

````shell script
# spark-defaults.conf
# Enter the MaxCompute project name and account information.
spark.hadoop.odps.project.name = XXX  # maxcompute project name
spark.hadoop.odps.access.id = XXX     # alibaba cloud account access id
spark.hadoop.odps.access.key = XXX    # alibaba cloud account access key

# Retain the following default settings.
Spark.hadoop.odps.end.point = http://service.cn.maxcompute.aliyun.com/api # Find corrent engpoints based on your maxcompute project region from: https://www.alibabacloud.com/help/doc-detail/34951.htm
spark.hadoop.odps.runtime.end.point = http://service.cn.maxcompute.aliyun-inc.com/api # Generally same as above
spark.sql.catalogImplementation=odps
spark.hadoop.odps.task.major.version = cupid_v2
spark.hadoop.odps.cupid.container.image.enable = true
spark.hadoop.odps.cupid.container.vm.engine.type = hyper

spark.hadoop.odps.cupid.webproxy.endpoint = http://service.cn.maxcompute.aliyun-inc.com/api
spark.hadoop.odps.moye.trackurl.host = http://jobview.odps.aliyun.com
```` 

For some functions, additional configuration might be needed. Refer to ![this documentation](https://github.com/aliyun/MaxCompute-Spark/wiki/07.-Spark%E9%85%8D%E7%BD%AE%E8%AF%A6%E8%A7%A3) for more detail.

#### 4. Clone this repository

This simplest way is to run:

````shell script
git clone https://github.com/iahsanujunda/maxcompute-spark.git
````

from your terminal.

## Building Package

This project is bootstrapped using ![Apache Maven](https://maven.apache.org/). To build this project, run:

````shell script
mvn clean package
````

This will resolve all dependencies, package a .jar executable, as well as run tests.

## Running Spark Programs on Maxcompute

To run your local development environment, Maxcompute provides two modes: Local-mode and Cluster-mode

#### Local Mode

In this mode, Spark on Maxcompute client runs on the local machine but make use of Tunnel to read and write data to your Maxcompute resources. In this mode, take a note on `local[N]` part, `N` indicates the number of CPU to be used by the client.

To execute, run:

````shell script
$SPARK_HOME/bin/spark-submit --master local[4] \
--class com.aliyun.odps.spark.examples.SparkPi \
${path to project directory}/target/maxcompute-spark-1.0-SNAPSHOT.jar
````

#### Cluster Mode

With cluster mode, the Spark program is run on the Maxcompute clusters, therefore take a note that this mode will need to upload the resource files to the Maxcompute cluster. It might take longer for this mode to finish compared to local mode, based on the internet connection. However, this mode will reflect the actual environment that the code will face on production environment.

To execute, run:

````shell script
$SPARK_HOME/bin/spark-submit --master yarn-cluster \
--class SparkPi \
${path to project directory}/target/maxcompute-spark-1.0-SNAPSHOT.jar
````

## Reference

1. ![Spark on Maxcompute Overview](https://www.alibabacloud.com/help/doc-detail/102357.htm?spm=a2c63.p38356.b99.274.1b565d68VKjNKo)
