import ml.dmlc.xgboost4j.scala.spark.{XGBoostRegressionModel, XGBoostRegressor}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.ml.feature.{Normalizer, StringIndexer, StringIndexerModel, VectorAssembler}
import org.apache.spark.ml.param.Param
import org.apache.spark.sql.functions.{collect_list, udf}
import org.spark_project.dmg.pmml.False

object Run {
  def main(args:Array[String]):Unit = {
    //val spark = SparkSession.builder().getOrCreate()
    val conf: SparkConf = new SparkConf().setAppName("test").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // Spark Session instance :
    val ss: SparkSession = org.apache.spark.sql
        .SparkSession.builder()
        .master("local")
        .appName("Read CSV")
        .enableHiveSupport()
        .getOrCreate()

    val features_array = Constants.NUMERIC_COLUMNS.toArray
    val columns_array = features_array :+ "SalePrice"
    //StructField(x, StringType, nullable = true)
    val columns_numeric:Array[StructField] = Constants.NUMERIC_COLUMNS.map(x=>StructField(x, DoubleType)).toArray
    val columns_string:Array[StructField] = Constants.STRING_COLUMNS.map(x=>StructField(x, StringType)).toArray
    val columns :Array[StructField] = columns_array.map(x=>StructField(x, DoubleType, nullable = true))

    /*
    val schema = new StructType(columns)
    val train_set: DataFrame = ss.read.schema(schema).csv(Constants.FILE_TRAIN)

    val features :Array[StructField] = features_array.map(x=>StructField(x, DoubleType, nullable = true))
    val schema_test = new StructType(features)
    val test_set: DataFrame = ss.read.schema(schema_test).csv(Constants.FILE_TEST)
    */

    val df_rough:DataFrame = ss.read
      .format("csv")
      .option("header", "true") //first line in file has headers
      .option("mode", "DROPMALFORMED")
      .option("inferSchema", value=true)
      .load(Constants.FILE_TRAIN)
      .toDF()

    val df_test:DataFrame = ss.read
      .format("csv")
      .option("header", "true") //first line in file has headers
      .option("mode", "DROPMALFORMED")
      .option("inferSchema", value=true)
      .load(Constants.FILE_TEST)
      .toDF()

    val df_test_new2 = df_test
    .withColumn("BsmtFinSF1", PreProcess.encodeNAToDouble(df_test("BsmtFinSF1")))
    .withColumn("BsmtFinSF2", PreProcess.encodeNAToDouble(df_test("BsmtFinSF2")))
    .withColumn("BsmtUnfSF", PreProcess.encodeNAToDouble(df_test("BsmtUnfSF")))
    .withColumn("TotalBsmtSF", PreProcess.encodeNAToDouble(df_test("TotalBsmtSF")))
    .withColumn("BsmtFullBath", PreProcess.encodeNAToDouble(df_test("BsmtFullBath")))
    .withColumn("BsmtHalfBath", PreProcess.encodeNAToDouble(df_test("BsmtHalfBath")))
    .withColumn("GarageCars", PreProcess.encodeNAToDouble(df_test("GarageCars")))
    .withColumn("GarageArea", PreProcess.encodeNAToDouble(df_test("GarageArea")))

  val df_train_new2 = df_rough
      .withColumn("BsmtFinSF1", PreProcess.encodeNAToDouble(df_rough("BsmtFinSF1")))
      .withColumn("BsmtFinSF2", PreProcess.encodeNAToDouble(df_rough("BsmtFinSF2")))
      .withColumn("BsmtUnfSF", PreProcess.encodeNAToDouble(df_rough("BsmtUnfSF")))
      .withColumn("TotalBsmtSF", PreProcess.encodeNAToDouble(df_rough("TotalBsmtSF")))
      .withColumn("BsmtFullBath", PreProcess.encodeNAToDouble(df_rough("BsmtFullBath")))
      .withColumn("BsmtHalfBath", PreProcess.encodeNAToDouble(df_rough("BsmtHalfBath")))
      .withColumn("GarageCars", PreProcess.encodeNAToDouble(df_rough("GarageCars")))
      .withColumn("GarageArea", PreProcess.encodeNAToDouble(df_rough("GarageArea")))

  def reduceNANS(ac:(DataFrame, String), curr:(DataFrame,String)): (DataFrame,String) = {
    val accumulator_df: DataFrame = ac._1
    val current_col:String = curr._2
    val new_df = accumulator_df.withColumn(current_col, PreProcess.encodeNAToDouble(accumulator_df(current_col)))
    (new_df, current_col)
  }

  val list_columns: List[String] = List(
    "BsmtFinSF1", "BsmtFinSF2",
    "BsmtUnfSF","TotalBsmtSF",
    "BsmtFullBath","BsmtHalfBath",
    "GarageCars","GarageArea")
//  indexed_string_columns.toArray.filterNot(_.equals("Id")))
  val resulted_df_no_nans = list_columns.map(e => (df_rough, e)).reduce(reduceNANS)
  val df_train_new = resulted_df_no_nans._1

  val resulted_df_no_nans_test = list_columns.map(e => (df_test, e)).reduce(reduceNANS)
  val df_test_new = resulted_df_no_nans_test._1

  val test_set_colum_types:Array[(String, String)] = df_test.dtypes
  val train_set_colum_types:Array[(String, String)] = df_rough.dtypes

  // Parse categorical features :
  val (input_train_string_indexer, input_test_string_indexer) = PreProcess.encodeWithStringIndexer(df_train_new, df_test_new)

  val indexed_string_columns = Constants.STRING_COLUMNS.map(_+"_indexed").toArray
  val (input_train_ohe, input_test_ohe) = PreProcess.encodeWithOHE(
    input_train_string_indexer,
    input_test_string_indexer,
    indexed_string_columns)

  // Parse numerical features :
  val ohe_columns:Array[String] = indexed_string_columns.map("ohe_"+_)
  val normalized_df_train = PreProcess.normalizeColumns(input_train_ohe, ohe_columns)
  val normalized_df_test = PreProcess.normalizeColumns(input_test_ohe, ohe_columns)

  // Assembler :
  val cols:Array[String] = Array("normalized_columns") ++ ohe_columns
  val assembler:VectorAssembler = new VectorAssembler()
    .setInputCols(cols)
    .setOutputCol("features")

  val xgbInput:DataFrame = assembler.transform(normalized_df_train).select("features", "SalePrice")
  val xgbTest:DataFrame = assembler.transform(normalized_df_test).select("features")

    xgbInput.show(false)

    val xgbParam = Map(
      "eta" -> 0.1f,
      "missing" -> 0.0,
      "alpha" -> 0.7,
      "lambda" -> 5.0,
      "maxDepth" -> 1000,
      "maxLeaves" -> 2000,
      //"objective" -> "reg:gamma",
      //"objective" -> "reg:tweedie",
      "objective" -> "reg:squarederror",
      "num_workers" -> 2,
      "silent" -> 1
    )

      val xgbInstance: XGBoostRegressor = new XGBoostRegressor(xgbParam)
        .setFeaturesCol("features")
        .setLabelCol("SalePrice")

      val prediction:DataFrame = Process.xgb_model(xgbInput:DataFrame, xgbInput:DataFrame, xgbInstance: XGBoostRegressor)
      val mse:Double = PreProcess.metricsMSELog(prediction)
      print("MSE = ")
      print(mse)

  }
}
