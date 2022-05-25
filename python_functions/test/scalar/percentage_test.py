from python_functions.udfs.scalar.percentage import percentage


def testLog():
    f = percentage._func
    assert f(9876.5432) == 98.77
