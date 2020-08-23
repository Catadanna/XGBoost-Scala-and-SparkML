name := "XGB"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.4"

libraryDependencies += "org.apache.spark" %% "spark-hive" % "2.4.0"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"

libraryDependencies += "ml.dmlc" % "xgboost4j-spark" % "0.90"

resolvers += Resolver.url("XGBoost4J-Spark Snapshot Repo", url("https://raw.githubusercontent.com/CodingCat/xgboost/maven-repo/"))
libraryDependencies += "ml.dmlc" % "xgboost4j-spark-SNAPSHOT" % "0.72"

