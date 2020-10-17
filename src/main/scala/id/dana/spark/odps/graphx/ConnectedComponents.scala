package id.dana.spark.odps.graphx

import org.apache.spark.graphx.GraphLoader
import org.apache.spark.sql.SparkSession

object ConnectedComponents {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Connected Components GraphX")
      .getOrCreate()
    val sc = spark.sparkContext

    // Load the graph as in the PageRank example
    val graph = GraphLoader.edgeListFile(sc, "src/main/resources/followers.txt")

    // Find the connected components
    val cc = graph.connectedComponents().vertices

    // Join the connected components with the usernames
    val users = sc.textFile("src/main/resources/users.txt").map { line =>
      val fields = line.split(",")
      (fields(0).toLong, fields(1))
    }
    val ccByUsername = users.join(cc).map {
      case (id, (username, cc)) => (username, cc)
    }

    // Print the result
    println(ccByUsername.collect().mkString("\n"))

    spark.stop()
  }
}
