import numpy as np
import pandas as pd
import pickle
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
import sklearn.linear_model as lm

df = pd.read_csv("dataset/final.csv", index_col=[0]).reset_index(drop=True)


def calc_acc(pred, exp):
    cnt = 0
    for i in range(len(pred)):
        if (np.absolute(pred[i]-exp[i]) < 2):
            cnt += 1

    return cnt/len(pred)


X = df.iloc[:, :-1].values
Y = df.iloc[:, -1].values

X_train, X_test, Y_train, Y_test = train_test_split(
    X, Y, test_size=0.2, random_state=0)

sc_X = StandardScaler()
X_train = sc_X.fit_transform(X_train)
X_test = sc_X.fit_transform(X_test)


classifier = lm.LogisticRegression()
classifier.fit(X_train, Y_train)
Y_pred = classifier.predict(X_test)

print(calc_acc(Y_pred, Y_test))

with open('model.pickle', 'wb') as f:
    pickle.dump(classifier, f, pickle.HIGHEST_PROTOCOL)

print("Model saved")
