def solution(S):
    # write your code in Python 2.7
    check_cap = None
    max_length = 0
    current_str = ""
    for char in S:
        if char.isupper() and check_cap is None:
            check_cap = True

        if char.isdigit():
            if len(current_str) > max_length and check_cap:
                max_length = len(current_str)
                current_str = ""
                check_cap = None
            else:
                current_str = ""
                check_cap = None
        else:
            current_str += char

    if len(current_str) > max_length and check_cap:
        max_length = len(current_str)

    if max_length = 0:
        retrun -1
    else:
        return max_length
