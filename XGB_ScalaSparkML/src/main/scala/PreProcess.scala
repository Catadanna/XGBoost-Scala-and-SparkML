import org.apache.hadoop.hdfs.web.resources.Param
import org.apache.spark.ml.feature.{Normalizer, OneHotEncoderEstimator, OneHotEncoderModel, StringIndexer, StringIndexerModel, VectorAssembler}
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf
import org.apache.spark.ml.classification.LogisticRegression

import scala.math._

object PreProcess {

  def encodeWithStringIndexer(df_train:DataFrame, df_test:DataFrame):(DataFrame,DataFrame) = {
    val indexers:List[(String, StringIndexerModel)] = Constants.STRING_COLUMNS
      .map(colName => (colName, new StringIndexer()
        .setInputCol(colName)
        .setOutputCol(colName+"_indexed")
        .setHandleInvalid("keep")
        .fit(df_train)
      )
    )
    val let_us_fold_train:DataFrame = indexers.foldLeft(df_train)(folderStringIndexer)
    val let_us_fold_test:DataFrame = indexers.foldLeft(df_test)(folderStringIndexer)
    (let_us_fold_train, let_us_fold_test)
  }

  def ordinalEncoder(col_name:String, indexer_name:StringIndexerModel, rawInput:DataFrame):DataFrame = {
    val labelTransformed:DataFrame = indexer_name.transform(rawInput).drop(col_name)
    labelTransformed
  }

  def folderStringIndexer(
                           accumulator_element:DataFrame,
                           current_element:(String, StringIndexerModel)):DataFrame = {
    val new_dataFrame = ordinalEncoder(current_element._1, current_element._2, accumulator_element)
    new_dataFrame
  }

  def folderStringIndexer_old(
                               accumulator_element:(String, StringIndexerModel, DataFrame),
                               current_element:(String, StringIndexerModel, DataFrame)
                             ):(String, StringIndexerModel, DataFrame) = {
    val new_dataFrame = ordinalEncoder(current_element._1, current_element._2, accumulator_element._3)
    (accumulator_element._1, accumulator_element._2, new_dataFrame)
  }

  def normalizeOneColumn(df:DataFrame, col_name:String):DataFrame = {
    val normalized_col_name:String = "normalized_"+col_name
    val normalizer:Normalizer = new Normalizer().setInputCol(col_name).setOutputCol(normalized_col_name)
    val new_df = normalizer.transform(df)
    new_df
  }

  def normalizeColumns(df:DataFrame, col_names:Array[String]):DataFrame = {
    val vectorAssembler: VectorAssembler = new VectorAssembler().
      setInputCols(col_names).
      setOutputCol("features_to_normalize")

    val assembled_features_to_normalize:DataFrame = vectorAssembler.transform(df)

    val normalizer:Normalizer = new Normalizer()
      .setInputCol("features_to_normalize")
      .setOutputCol("normalized_columns")
      .setP(1.0)
    val new_df = normalizer.transform(assembled_features_to_normalize)
    new_df
  }

  def encodeWithOHE(
                     df_train:DataFrame,
                     df_test:DataFrame,
                     col_names:Array[String]
                   ): (DataFrame, DataFrame) = {
    val output_cols = col_names.map("ohe_"+_)

    val ohe_encoder:OneHotEncoderEstimator = new OneHotEncoderEstimator()
      .setInputCols(col_names)
      .setOutputCols(output_cols)
      .setHandleInvalid("keep")

    val ohe_model:OneHotEncoderModel = ohe_encoder.fit(df_train)
    val new_df_train = ohe_model.transform(df_train)
    val new_df_test = ohe_model.transform(df_test)

    (new_df_train, new_df_test)
  }

  val encodeStringToDouble: UserDefinedFunction = udf[Double, String](_.toDouble)

  val encodeNAToDouble: UserDefinedFunction = udf[Double, String] {
    case "NA" => 0.0
    case e => e.toDouble
  }

  def metricsMSE(result:DataFrame):Double = {
    val rm:RegressionMetrics = new RegressionMetrics(
      result
        .select("SalePrice","prediction")
        .rdd
        .map(x => (x(0).toString.toDouble, x(1).asInstanceOf[Double])))

    val mse = Math.sqrt(rm.meanSquaredError)
    mse
  }

  def metricsMSELog(result:DataFrame):Double = {
    val rm:RegressionMetrics = new RegressionMetrics(
      result
        .select("SalePrice","prediction")
        .rdd
        .map(x => (log(x(0).toString.toDouble), log(x(1).asInstanceOf[Double]))))

    val mse = Math.sqrt(rm.meanSquaredError)
    mse
  }

  def metricsMSENormalized(result:DataFrame):Double = {
    val rm:RegressionMetrics = new RegressionMetrics(
      result
        .select("SalePrice","prediction")
        .rdd
        //.map(x => (x(0).asInstanceOf[Double], x(1).asInstanceOf[Double])))
        .map(x => (x(0).toString.toDouble, x(1).asInstanceOf[Double]*10)))
    val mse = Math.sqrt(rm.meanSquaredError)
    mse
  }

  def metricsMSENormalizedLog(result:DataFrame):Double = {
    val rm:RegressionMetrics = new RegressionMetrics(
      result
        .select("SalePrice","prediction")
        .rdd
        //.map(x => (x(0).asInstanceOf[Double], x(1).asInstanceOf[Double])))
        .map(x => (log(x(0).toString.toDouble), log(x(1).asInstanceOf[Double]*10))))
    val mse = Math.sqrt(rm.meanSquaredError)
    mse
  }

  def getlog(df:DataFrame): Unit = {
    val lr = new LogisticRegression()
      .fit(df)
  }



}

