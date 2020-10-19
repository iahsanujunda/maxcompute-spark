package id.dana.spark.odps.sql

import org.apache.log4j.{LogManager, Logger}
import org.apache.spark.sql.{AnalysisException, Row, SparkSession}
import org.apache.spark.sql.types._

object MaxcomputeDataSource {
  val log: Logger = LogManager.getRootLogger

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("SparkSQL-on-MaxCompute")
      .config("spark.sql.broadcastTimeout", 20 * 60)
      .config("spark.sql.crossJoin.enabled", value = false)
      .config("spark.sql.catalogImplementation", "odps")
      .getOrCreate()
    val project = spark.conf.get("odps.project.name")
    val tableName = "ods_people"

    prepareResources(spark, tableName)
    analyzeData(spark, tableName)

    // Lest we forget!!!
    spark.stop()
  }

  private def prepareResources(spark: SparkSession, odpsResource: String): Unit = {
    // Drop Create
    spark.sql(s"DROP TABLE IF EXISTS $odpsResource")
    spark.sql(s"CREATE TABLE $odpsResource (name STRING, age INT, job STRING)")

    // Read files and drop header
    var rdd = spark.sparkContext.emptyRDD[String]
    try {
      rdd = spark.sparkContext.textFile("src/main/resources/people.csv")
    } catch {
      case _: AnalysisException => log.error("File not found")
      case ex: Exception => log.error(ex.getMessage)
    }
    val peopleRDD = rdd.mapPartitionsWithIndex{
      (idx, iter) => if (idx == 0) iter.drop(1) else iter
    }

    // Prepare schema
    val schemaString = "name age job"
    val fields = schemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)

    // Generate RDD data
    val rowRDD = peopleRDD
      .map(_.split(";"))
      .map(attributes => Row(attributes(0).trim, attributes(1).trim, attributes(2).trim))

    // Convert RDD to DF
    val peopleDf = spark.createDataFrame(rowRDD, schema)
    peopleDf.show(10)

    // Insert Into
    peopleDf.write.insertInto(odpsResource)
  }

  private def analyzeData(spark: SparkSession, odpsResource: String): Unit = {
    // select
    val sql_df = spark.sql(s"select * from $odpsResource")
    println("====================================")
    println("|             Schema               |")
    println("====================================")
    sql_df.printSchema()

    println("====================================")
    println("|           Full Table             |")
    println("====================================")
    sql_df.show(10)

    // filter
    println("====================================")
    println("|             Filter               |")
    println("====================================")
    sql_df.filter("age >= 25").show()

    // aggregate
    println("=====================================")
    println("|           Aggregate               |")
    println("=====================================")
    sql_df.groupBy("job").count().show()
  }
}
