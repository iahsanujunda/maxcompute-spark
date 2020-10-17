package id.dana.spark.odps.graphx

import org.apache.spark.graphx.GraphLoader
import org.apache.spark.sql.SparkSession

object PageRankGraphX {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName(s"Page Rank GraphX")
      .getOrCreate()
    val sc = spark.sparkContext

    // Load my user data and parse into tuples of user id and attribute list
    val users = sc.textFile("src/main/resources/users.txt")
      .map(line => line.split(","))
      .map(parts => (parts.head.toLong, parts.tail))

    // Parse the edge data which is already in userId -> userId format
    val followerGraph = GraphLoader
      .edgeListFile(sc, "src/main/resources/followers.txt")

    // Attach the user attributes
    val graph = followerGraph
      .outerJoinVertices(users) {
        case (uid, deg, Some(attrList)) => attrList
        // Some users may not have attributes so we set them as empty
        case (uid, deg, None) => Array.empty[String]
    }

    // Restrict the graph to users with usernames and names
    val subgraph = graph
      .subgraph(vpred = (vid, attr) => attr.length == 2)

    // Compute the PageRank
    val pagerankGraph = subgraph.pageRank(0.001)

    // Get the attributes of the top pagerank users
    val userInfoWithPageRank = subgraph.outerJoinVertices(pagerankGraph.vertices) {
      case (uid, attrList, Some(pr)) => (pr, attrList.toList)
      case (uid, attrList, None) => (0.0, attrList.toList)
    }

    println(userInfoWithPageRank.vertices.top(6)(Ordering.by(_._2._1)).mkString("\n"))

    spark.stop()
  }
}
