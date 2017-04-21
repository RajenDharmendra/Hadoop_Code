
ll = [-1, 3, -4, 5, 1, -6, 2, 1]

i = len(A) / 2
while 1:

    if i == -1:
        print i
        break
    elif sum(A[0:i]) == sum(A[i+1:]):
        print i
        break
    else:
        i = i - 1
