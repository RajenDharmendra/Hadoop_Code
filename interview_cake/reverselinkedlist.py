class LinkedList(object):

    def __init__(self, value, next_):
        self.value = value
        self.next = next_

ll = [1, 2, 3, 4, 5, 6]
head_node = LinkedList(ll[0], None)
for i in range(len(ll)):
    if i == 0:
        previous_node = head_node
    else:
        new_node = LinkedList(ll[i], None)
        previous_node.next = new_node
        previous_node = new_node

current = head_node
while current:
    print current.value
    current = current.next

current = head_node
next_ = None
previous = None
while current:
    next_ = current.next_
    current.next = previous
    previous = current
    current = next_
