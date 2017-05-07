def solution(A, B, M, X, Y):
    # write your code in Python 2.7
    i = 0
    stops_count = 0
    length_a = len(A)
    while i < length_a:
        sum_weight = 0
        target_set = set()
        for j in range(i, i + X):
            if j < length_a:
                sum_weight += A[j]
                if sum_weight < Y:
                    target_set.add(B[j])
                    i = j
                else:
                    break
            else:
                break

        if len(target_set) == 1:
            stops_count += 2
        else:
            stops_count += len(target_set) + 1

        i = i + 1

    return stops_count



print(solution([40,40,100,80,20], [3,3,2,2,3], 3, 5, 200))
