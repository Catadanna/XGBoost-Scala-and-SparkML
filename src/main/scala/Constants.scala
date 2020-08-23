import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import scala.collection.immutable._

object Constants {
  val FILE_TRAIN = "src/main/inc/train.csv"
  val FILE_TEST = "src/main/inc/test.csv"

  val NAN_STR_REPLACEMENT = "8888.0"
  val NAN_INT_REPLACEMENT = 8888
  val NAN_FLOAT_REPLACEMENT = 8888.0

  val LABELS_COLUMN = "SalePrice"

  /*
  PARAMS_CATBOOST = dict()
  PARAMS_CATBOOST['logging_level'] = 'Silent'
  PARAMS_CATBOOST['use_best_model']: True
  PARAMS_CATBOOST['od_type'] = 'Iter' # IncToDec, Iter
  PARAMS_CATBOOST['loss_function'] = 'RMSE'
  PARAMS_CATBOOST['iterations'] = 2000
  '''
  PARAMS_CATBOOST['eval_metric'] = 'AUC'
  PARAMS_CATBOOST['iterations'] = 2000
  PARAMS_CATBOOST['random_seed'] = SEED
  PARAMS_CATBOOST['l2_leaf_reg'] = 30
  PARAMS_CATBOOST['learning_rate'] = 0.1
  PARAMS_CATBOOST['task_type'] = 'CPU'
  PARAMS_CATBOOST['depth'] = 3
  PARAMS_CATBOOST['bagging_temperature'] = 0.8
  PARAMS_CATBOOST['random_strength']: 0.8
  PARAMS_CATBOOST['bootstrap_type'] = 'Bayesian'
  PARAMS_CATBOOST['nan_mode'] = 'Min'
  PARAMS_CATBOOST['thread_count'] = 4
  '''

  PARAMS_HUBER = dict()
  PARAMS_HUBER['epsilon'] = 1.35 # default 1.35
  PARAMS_HUBER['max_iter'] = 50000 # default 100
  PARAMS_HUBER['alpha'] = 0.0001 # default 0.0001

  PARAMS_LSVR = dict()
  #PARAMS_LSVR['epsilon'] = 1.35 # default 0.0
  PARAMS_LSVR['max_iter'] = 100000 # default 1000
  PARAMS_LSVR['C'] = 1.0 # default 1.0 OK : 4, 0.1
  PARAMS_LSVR['verbose'] = 0 # default 0
*/

  //val CAT_FEATURES = [1, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 26, 27, 28, 29, 30, 31, 32, 34, 38, 39, 40, 41, 52, 54, 56, 57, 59, 62, 63, 64, 71, 72, 73, 77, 78]


  val STRING_COLUMNS: List[String] = List(
    "MSZoning",
    "LotFrontage",
    "Alley",
    "LotShape",
    "MasVnrArea",
    //"Street", //- only one, delete
    "LandContour",
    //"Utilities", // -only one, delete
    "LotConfig",
    "LandSlope",
    "Neighborhood",
    "Condition1",
    "Condition2",
    "BldgType",
    "HouseStyle",
    "RoofStyle",
    "RoofMatl",
    "Exterior1st",
    "Exterior2nd",
    "MasVnrType",
    "ExterQual",
    "ExterCond",
    "Foundation",
    "BsmtQual",
    "BsmtCond",
    "BsmtExposure",
    "BsmtFinType1",
    "BsmtFinType2",
    "Heating",
    "HeatingQC",
    "CentralAir",
    "Electrical",
    "KitchenQual",
    "Functional",
    "FireplaceQu",
    "GarageType",
    "GarageFinish",
    "GarageQual",
    "GarageCond",
    "PavedDrive",
    "PoolQC", // - only NA 99.9%, delete
    "Fence",
    "MiscFeature",
    "YrSold",
    "SaleType",
    "GarageYrBlt",
    "SaleCondition"
  )

  val NUMERIC_COLUMNS: List[String] = List(
    "MSSubClass",
    //"LotFrontage", // string
    "LotArea",
    "OverallQual",
    "OverallCond",
    "YearBuilt",
    "YearRemodAdd",
    //"MasVnrArea", // string
    "BsmtFinSF1",
    "BsmtFinSF2",
    "BsmtUnfSF",
    "TotalBsmtSF",
    "1stFlrSF",
    "2ndFlrSF",
    "LowQualFinSF",
    "GrLivArea",
    "BsmtFullBath",
    "BsmtHalfBath",
    "FullBath",
    "HalfBath",
    "BedroomAbvGr",
    "KitchenAbvGr",
    "TotRmsAbvGrd",
    "Fireplaces",
    //"GarageYrBlt", // string
    "GarageCars",
    "GarageArea",
    "WoodDeckSF",
    "OpenPorchSF",
    "EnclosedPorch",
    "3SsnPorch",
    "ScreenPorch",
    "PoolArea",
    "MiscVal",
    "MoSold"
    //"nb_missing_values"
  )
}
