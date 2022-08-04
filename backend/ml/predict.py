import pickle
import sys

input = []

for line in sys.stdin:
    # print("reveive a line: " + line)
    if(line == "\n"):
        break
    line = line.split(',')
    for i in range(len(line)):
        line[i] = int(line[i])
    input.append(line)

# print(input)

with open('ml/model.pickle', 'rb') as f:
    model = pickle.load(f)

print(''.join(map(str, model.predict(input).tolist())))
