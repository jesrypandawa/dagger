from pyflink.table import DataTypes
from pyflink.table.udf import udf


@udf(result_type=DataTypes.FLOAT())
def percentage(input_number):
    return round((input_number/100), 2)
