def solution(A, B):
    # write your code in Python 2.7

    A = str(A)
    B = str(B)
    if len(A) > len(B):
        min_str = B
        max_str = A
    else:
        min_str = A
        max_str = B
    out_str = ""
    for i in range(len(min_str)):
        out_str += A[i] + B[i]

    result = int(out_str + max_str[i+1:])

    if result > 100000000:
        return -1
    else:
        return result









print(solution(12345, 672))
