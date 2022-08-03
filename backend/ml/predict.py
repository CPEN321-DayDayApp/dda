import pickle
import sys

input = []

for line in sys.stdin:
    # print(line)
    line = line.split(',')
    for i in range(len(line)):
        line[i] = int(line[i])
    input.append(line)

with open('model.pickle', 'rb') as f:
    model = pickle.load(f)

print(model.predict(input).tolist())
