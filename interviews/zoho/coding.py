# finding power without sqrt function


def power_(x, y):
    value = 1
    i = 1
    while i <= y:
        value *= x
        i += 1
    return value


def do_power(x, y):
    if y < 0:
        y = -y
        print 1.0 / power_(x, y)
    elif y > 0:
        print power_(x, y)
    elif y == 0:
        print x


do_power(2, 3)
do_power(2, -2)
do_power(-2, -1)
