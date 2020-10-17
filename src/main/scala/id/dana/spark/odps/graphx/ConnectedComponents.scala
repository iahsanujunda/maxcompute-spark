package id.dana.spark.odps.graphx

import org.apache.spark.graphx.GraphLoader
import org.apache.spark.sql.SparkSession

object ConnectedComponents {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Connected Components GraphX")
      .config("spark.executor.memory", "8g")
      .getOrCreate()
    val sc = spark.sparkContext

    // Load the graph
    val graph = GraphLoader.edgeListFile(sc, "src/main/resources/twitter-short.txt")

    // Find the connected components
    val cc = graph.connectedComponents().vertices

    // Print the result
    println(cc.collect().mkString("\n"))

    spark.stop()
  }
}
