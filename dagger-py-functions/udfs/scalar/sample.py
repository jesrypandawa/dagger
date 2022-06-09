from pyflink.table import DataTypes
from pyflink.table.udf import udf


@udf(result_type=DataTypes.STRING())
def sample(text):
    file = open("data/test_file.txt", "r")
    data = file.read()
    return text + '_' + data
