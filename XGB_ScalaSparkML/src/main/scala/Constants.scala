import scala.collection.immutable._

object Constants {
  val FILE_TRAIN = "src/main/inc/train.csv"
  val FILE_TEST = "src/main/inc/test.csv"

  val NAN_STR_REPLACEMENT = "8888.0"
  val NAN_INT_REPLACEMENT = 8888
  val NAN_FLOAT_REPLACEMENT = 8888.0

  val LABELS_COLUMN = "SalePrice"

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
    // "PoolQC", // - only NA 99.9%, delete
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
