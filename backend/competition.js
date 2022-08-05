const {
    PythonShell
} = require('python-shell')
// const LeaderBoard = require("./leaderboard")
// const Users = require("./Users")
// const Database = require('./Database');
// const LeaderBoard = require("./leaderboard")

const predict = new PythonShell('ml/predict.py');
// const retrain = new PythonShell('ml/retrain.py');

// const DB_URL = "mongodb://localhost:27017";
// const DB_NAME = "data"
// const DB_NAME2 = "leaderboard"
const ELO_CONSTANT = 32;
const DAILY_LIMIT = 720;
const SCORE_CONSTANT = 15;
const LEVEL_RANGE = 5;

// const db = new Database(DB_URL, DB_NAME)
// const db2 = new Database(DB_URL, DB_NAME2)
// const user = new Users(db)
// const leaderboard = new LeaderBoard(db2)

function Competition(userdb, leaderdb) {
    this.assignNewOpponent = function () {
        userdb.getAllUsers().then(users => {
            //console.log(users)
            let input = [];
            users.forEach(element => {
                input.push(element.age)
                input.push(',')
                if (element.gender == 'male') input.push(0)
                else input.push(1)
                input.push(',')
                input.push(Math.round(element.score * SCORE_CONSTANT / 7))
                input.push('\n')
            });
            // console.log(input)
            // console.log(input.join(''))
            predict.send(input.join(''));
            predict.on('message', function (message) {
                // console.log(message);
                let result = Array.from(message).map(Number);
                console.log(result);

                while (Math.min(...result) != 9) {
                    let minLevel = Math.min(...result);
                    let minIndex = result.indexOf(minLevel);
                    result[minIndex] = 9;
                    console.log('Current Lowest Level: ' + users[minIndex].userid);
                    if (Math.min(...result) != 9) {
                        let opponent = result.indexOf(Math.min(...result));
                        result[opponent] = 9;
                        console.log('Current Opponent: ' + users[opponent].userid);
                        //db.editOpponentId(users[minIndex].userid, users[opponent].userid);
                        //db.editOpponentId(users[opponent].userid, users[minIndex].userid);
                    }
                }
            })
            predict.end(function (err, code, signal) {
                if (err) throw err;
                console.log('The exit code was: ' + code);
                console.log('The exit signal was: ' + signal);
                console.log('finished');
            });
        });
    }

    this.settleMatch = function () {
        let updatedScore = [];

        leaderdb.getGlobalBoard().then(result => {
            result.globalboard.forEach(element => {
                element['modified'] = 0;
            });
            let gb = result.globalboard;
            let retrainInput = [];
            userdb.getAllUsers().then(users => {
                users.forEach(user => {
                    retrainInput.push(user.age)
                    retrainInput.push(',')
                    if (user.gender == 'male') retrainInput.push(0)
                    else retrainInput.push(1)
                    retrainInput.push(',')
                    retrainInput.push(Math.round(user.score * SCORE_CONSTANT / 7))
                    retrainInput.push(',')
                    let tmp = Math.round(LEVEL_RANGE * user.score * SCORE_CONSTANT / (7 * DAILY_LIMIT));
                    if (tmp > LEVEL_RANGE) tmp = LEVEL_RANGE;
                    if (tmp < 0) tmp = 0;
                    retrainInput.push(tmp)
                    retrainInput.push('\n')
                    let findUser = gb.filter(obj => {
                        return obj._id == user.userid
                    });
                    if (findUser.length !== 0 && findUser[0].modified === 0) {
                        let currUser = user;
                        let rankScore1 = gb.filter(obj => {
                            return obj._id == user.userid
                        })[0].score;
                        let score1 = currUser.score;
                        userdb.getOpponent(currUser.userid).then(opponent => {
                            let findOpponent = gb.filter(obj => {
                                return obj._id == opponent.id
                            });
                            if (findOpponent.length !== 0 && findOpponent[0].modified === 0) {
                                let currOpponent = opponent;
                                let rankScore2 = gb.filter(obj => {
                                    return obj._id == currOpponent.id
                                })[0].score;
                                let score2 = currOpponent.score;
                                let win = 0;
                                if (score1 > score2) win = 1;
                                if (score1 != score2) {
                                    let p1 = rankScore1 / (rankScore1 + rankScore2);
                                    let p2 = rankScore2 / (rankScore1 + rankScore2);
                                    rankScore1 = Math.round(rankScore1 + ELO_CONSTANT * (win - p1));
                                    rankScore2 = Math.round(rankScore2 + ELO_CONSTANT * (1 - win - p2));
                                    for (let i = 0; i < gb.length; i++) {
                                        if (gb[i]._id == currUser.userid) {
                                            gb[i].score = rankScore1;
                                            gb[i].modified = 1;
                                        } else if (gb[i]._id == currOpponent.id) {
                                            gb[i].score = rankScore2;
                                            gb[i].modified = 1;
                                        }
                                    }
                                    updatedScore.push({
                                        "userid": currUser.userid,
                                        "score": rankScore1
                                    });
                                    updatedScore.push({
                                        "userid": currOpponent.id,
                                        "score": rankScore2
                                    });
                                } else {
                                    for (let i = 0; i < gb.length; i++) {
                                        if (gb[i]._id == currUser.userid) {
                                            gb[i].modified = 1;
                                        } else if (gb[i]._id == currOpponent.id) {
                                            gb[i].modified = 1;
                                        }
                                    }
                                    updatedScore.push({
                                        "userid": currUser.userid,
                                        "score": rankScore1
                                    });
                                    updatedScore.push({
                                        "userid": currOpponent.id,
                                        "score": rankScore2
                                    });
                                }
                                console.log(updatedScore)
                                leaderdb.scoreUpdate(updatedScore).then(result => {
                                    console.log(result)
                                });
                                updatedScore = [];
                            }
                        })
                    }
                })
                retrain.send(retrainInput.join(''));
                retrain.on('message', function (message) {
                    console.log(message);
                })
                retrain.end(function (err, code, signal) {
                    if (err) throw err;
                    console.log('The exit code was: ' + code);
                    console.log('The exit signal was: ' + signal);
                    console.log('finished');
                });
            }).catch(err => {
                console.log(err)
            })
        }).catch(err => {
            console.log(err)
        })
    }

}

module.exports = Competition;