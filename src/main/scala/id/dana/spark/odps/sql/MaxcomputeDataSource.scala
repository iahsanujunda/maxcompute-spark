package id.dana.spark.odps.sql

import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

object MaxcomputeDataSource {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("SparkSQL-on-MaxCompute")
      .config("spark.sql.broadcastTimeout", 20 * 60)
      .config("spark.sql.crossJoin.enabled", false)
      .config("spark.sql.catalogImplementation", "odps")
      .getOrCreate()
    val project = spark.conf.get("odps.project.name")
    val tableName = "ods_people"

    prepareRecources(spark, tableName)
    retrieveData(spark, tableName)

    // Lest we forget!!!
    spark.stop()
  }

  private def prepareRecources(spark: SparkSession, odpsResource: String): Unit = {
    // Drop Create
    spark.sql(s"DROP TABLE IF EXISTS $odpsResource")
    spark.sql(s"CREATE TABLE $odpsResource (name STRING, age INT, job STRING)")

    // Read files
    val peopleRDD = spark.sparkContext.textFile("src/main/resources/people.csv")
    val schemaString = "name age job"

    // Generate the schema based on the string of schema
    val fields = schemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)

    // Generate RDD-based data
    val rowRDD = peopleRDD
      .map(_.split(";"))
      .map(attributes => Row(attributes(0).trim, attributes(1).trim, attributes(2).trim))

    // Convert RDD to DF
    val peopleDf = spark.createDataFrame(rowRDD, schema)
    peopleDf.show(10)

    // Insert Into
    peopleDf.write.insertInto(odpsResource)
  }

  private def retrieveData(spark: SparkSession, odpsResource: String): Unit = {
    val result_df = spark.sql(s"select * from $odpsResource")
    result_df.show(10)
  }
}
