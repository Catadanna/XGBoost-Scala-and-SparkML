import ml.dmlc.xgboost4j.scala.spark.{XGBoostRegressionModel, XGBoostRegressor}
import org.apache.spark.ml.{Pipeline, PipelineModel, PipelineStage}
import org.apache.spark.sql.DataFrame

object Process {
  /*
  * Description: Fits the model on the input data and returns the predictions on the test data
  * */
  def xgb_model(xgbInput:DataFrame, xgbTest:DataFrame, xgbRegressor: XGBoostRegressor):DataFrame = {
    val XGBModel:XGBoostRegressionModel = xgbRegressor.fit(xgbInput)
    XGBModel.transform(xgbTest)
  }

  /*
  * Description: Fits the model on the input data and returns the predictions on the test data
  * */
  def xgb_pipeline(
                    df_train:DataFrame,
                    df_test:DataFrame,
                    xgbRegressor: XGBoostRegressor,
                    stages:Array[PipelineStage]
                  ):DataFrame = {
    val all_stages = stages :+ xgbRegressor
    val pipeline:Pipeline = new Pipeline().setStages(all_stages)
    val XGBModel:PipelineModel = pipeline.fit(df_train)
    val predictions:DataFrame = XGBModel.transform(df_test)
    predictions
  }
}
