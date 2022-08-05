const {
    PythonShell
} = require('python-shell')
// const LeaderBoard = require("./leaderboard")
// const Users = require("./Users")
// const Database = require('./Database');
// const LeaderBoard = require("./leaderboard")

const predict = new PythonShell('ml/predict.py');
const retrain = new PythonShell('ml/retrain.py');

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
        return new Promise((resolve, reject) => {
            userdb.getAllUsers().then(users => {
                if(users.length != 0){
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
                        var num=0;
                        while (Math.min(...result) != 9) {
                            num++;
                            let minLevel = Math.min(...result);
                            let minIndex = result.indexOf(minLevel);
                            result[minIndex] = 9;
                            console.log('Current Lowest Level: ' + users[minIndex].userid);
                            if (Math.min(...result) != 9) {
                                let opponent = result.indexOf(Math.min(...result));
                                result[opponent] = 9;
                                console.log('Current Opponent: ' + users[opponent].userid);
                                Promise.all([userdb.editOpponentId(users[minIndex].userid, users[opponent].userid),
                                userdb.editOpponentId(users[opponent].userid, users[minIndex].userid)]).then(values=>{
                                    if(num===(result.length+1)/2) resolve(200);

                                })   
                            }
                        }
                    })
                    predict.end(function (err, code, signal) {
                        if (err) reject("Error: " + err);
                        console.log('The exit code was: ' + code);
                        console.log('The exit signal was: ' + signal);
                    });
                }
            });
        })
    }

    this.settleMatch = function () {
        return new Promise((resolve, reject) => {
            let updatedScore = [];
            let retrainInput = [];
            Promise.all([leaderdb.getGlobalBoard(),userdb.getAllUsers()]).then(result => {
                let gb = result[0].globalboard;
                var num=0;
                result[1].forEach(user => {
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
                    let findUser = gb.find(obj => obj._id === user.userid);
                    let currUser = user;
                    let rankScore1 = findUser.score;
                    let score1 = currUser.score;
                    userdb.getOpponent(currUser.userid).then(opponent => {
                        num++;
                        let findOpponent = gb.find(obj => obj._id == opponent.id);
                        if (findUser!== undefined && updatedScore.find(response =>response['userid']===user.userid)===undefined&&findOpponent!== undefined && updatedScore.find(response =>response['userid']===opponent.id)===undefined) {
                            let currOpponent = opponent;
                            let rankScore2 = findOpponent.score;
                            let score2 = currOpponent.score;
                            let win = 0;
                            if (score1 > score2) win = 1;
                            if (score1 != score2) {
                                let p1 = rankScore1 / (rankScore1 + rankScore2);
                                let p2 = rankScore2 / (rankScore1 + rankScore2);
                                rankScore1 = Math.round(rankScore1 + ELO_CONSTANT * (win - p1));
                                rankScore2 = Math.round(rankScore2 + ELO_CONSTANT * (1 - win - p2));
                            }
                            updatedScore.push({
                                "userid": currOpponent.id,
                                "score": rankScore2
                            });
                            updatedScore.push({
                                "userid": currUser.userid,
                                "score": rankScore1
                            });
                            console.log(updatedScore)
                        }
                        if(num===result[1].length){
                            this.retrainModel(retrainInput);
                            leaderdb.updateAllBoard(updatedScore).then(result => {
                                resolve(result)
                            });
                        }
                    })            
                })
            }).catch(err => {
                reject(err);
            })
        })
    }

    this.retrainModel=function(retrainInput){
        retrain.send(retrainInput.join(''));
        retrain.on('message', function (message) {
            console.log(message);
        })
        retrain.end(function (err, code, signal) {
            if (err) throw err;
            console.log('The exit code was: ' + code);
            console.log('The exit signal was: ' + signal);
        });
    }

}

module.exports = Competition;