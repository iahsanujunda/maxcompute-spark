# Apache Spark on Alibaba Cloud Maxcompute

<p align="center">
<img src="https://github.com/apache/spark/blob/master/docs/img/spark-logo-hd.png" width="60%" alt="Apache Spark" />
</p>

Spark on MaxCompute is a computing service provided by MaxCompute. It is compatible with the open-source Spark. It provides a Spark computing framework based on unified computing resources and a dataset permission system, which allows you to submit and run Spark jobs in your preferred development method. Spark on MaxCompute can fulfill the diverse needs of data processing and analysis.<sup>1</sup>

In this project, we are going to implement some common Spark operations, such as RDD, DataFrame, SparkSQL and MlLib, as well as some Maxcompute specific operations such as ODPS (big data platform - not unlike bigquery and redshift for those who familiar with them), OSS (object storage), and Dataworks (unified orchestrator - airflow, if you may) 

## Building Package

This project is bootstrapped using ![Apache Maven](https://maven.apache.org/). To build this project, run:

    mvn clean package

This will resolve all dependencies, package a .jar executable, as well as run tests.

## Reference

1 ![Spark on Maxcompute Overview](https://www.alibabacloud.com/help/doc-detail/102357.htm?spm=a2c63.p38356.b99.274.1b565d68VKjNKo)
