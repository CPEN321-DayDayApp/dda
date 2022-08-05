const express = require('express');
const Database = require('./Database');
const TodoList = require("./todoList");
const Users = require("./Users")
const Friend = require("./friend")
const Info = require("./info")
const LeaderBoard = require("./leaderboard")
const Competition = require("./competition")

// const {
//     PythonShell
// } = require('python-shell')
const CronJob = require('cron').CronJob;

//const { exec } = require("child_process");
const {
    OAuth2Client
} = require('google-auth-library');
const {
    CLIENT_ID1,
    CLIENT_ID2,
    CLIENT_ID3
} = require('./local_info')
const client = new OAuth2Client(CLIENT_ID1);

const DB_URL = "mongodb://localhost:27017";
const DB_NAME = "data"
const DB_NAME2 = "leaderboard"

const app = express();
const db = new Database(DB_URL, DB_NAME)
const db2 = new Database(DB_URL, DB_NAME2)
const user = new Users(db)
const tdl = new TodoList(user)
const friend = new Friend(user)
const info = new Info(user)
const leaderboard = new LeaderBoard(db2)
const competition = new Competition(db, leaderboard)

async function verify(token) {
    const ticket = await client.verifyIdToken({
        idToken: token,
        audience: [CLIENT_ID1, CLIENT_ID2, CLIENT_ID3], // Specify the CLIENT_ID of the app that accesses the backend
        // Or, if multiple clients access the backend:
        //[CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]
    });
    const payload = ticket.getPayload();
    const userid = payload['sub'];
    const useremail = payload['email'];
    const username = payload['name'];
    var res = {
        userid,
        "email": useremail,
        "name": username
    };
    return res;
    // If request specified a G Suite domain:
    // const domain = payload['hd'];
}

var newWeek = new CronJob(
    '59 59 23 * * 0',
    function () {
        competition.settleMatch();
        competition.assignNewOpponent();
        db.resetScore();
    },
    null,
    true,
    'America/Los_Angeles'
);

var newYear = new CronJob(
    '59 59 23 31 11 *',
    function () {
        db.increaseAge();
    },
    null,
    true,
    'America/Los_Angeles'
);

// app.use(express.json());
app.use(express.json({
    limit: '50mb'
}))

app.get("/user", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            db.getUser(result.userid).then(result => {
                res.status(200).send(JSON.stringify(result))
            })
        })
        .catch(console.error);
});

//add new user
app.post("/user", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            if (!isNaN(result.userid) && typeof (userid) != Number && (/\S[^\s@]*@\S+\.\S+/.test(result.email))) {
                db.addUser(result.userid, result.email, result.name, req.body.token).then(response => {
                    leaderboard.newPlayer(result.userid, result.name, 0).then(response => {
                        res.status(200).send("User added successfully\n")
                    })
                })
            } else {
                res.status(400).send("User added unsuccessfully\n")
            }
        })
        .catch(console.error);

});

app.get("/location", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            info.getLocation(result.userid).then(result => {
                if (result == 404) res.status(404).send(JSON.stringify({"location": []}))
                else res.status(200).send(JSON.stringify(result))
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

app.get("/leaderboard/global", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            leaderboard.getGlobalBoard().then(result => {
                if (result == 201) res.status(201).send(JSON.stringify({"globalboard": []}))
                else res.status(200).send(JSON.stringify(result))
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

app.get("/rank/global", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            leaderboard.getGlobalRank(result.userid).then(result => {
                res.status(200).send(JSON.stringify(result))
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

app.get("/leaderboard/friend", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            leaderboard.getFriendBoard(result.userid).then(result => {
                if (result == 201) res.status(201).send(JSON.stringify({"friendboard": []}))
                else res.status(200).send(JSON.stringify(result))
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

app.get("/rank/friend", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            leaderboard.getFriendRank(result.userid).then(result => {
                res.status(200).send(JSON.stringify(result))
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

app.get("/opponent", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            db.getOpponent(result.userid).then(result => {
                res.status(200).send(JSON.stringify(result))
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

app.get("/user/flag", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            db.getFlag(result.userid).then(result => {
                res.status(200).send(JSON.stringify(result))
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//add new location
app.post("/location", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            info.addLocation(result.userid, req.body.lat, req.body.lng).then(result => {
                if (result == 404) res.status(404).send("User not found")
                else res.status(200).send("Location added successfully\n")
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//delete new location
app.delete("/location", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            info.deleteLocation(result.userid, parseFloat(req.headers.lat), parseFloat(req.headers.lng)).then(result => {
                if (result == 404) res.status(404).send("User not found")
                else res.status(200).send("Location deleted successfully\n")
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//add new task to the tdl
app.post("/tdl", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            tdl.addTDL(result.userid, req.body.taskId, req.body.lat, req.body.lng, req.body.task, req.body.time, req.body.date).then(result => {
                var message;
                if (result == 404) message = "User not found\n";
                else if (result == 405) message = "Task already added, Use PUT request to edit\n";
                else message = "TDL added successfully\n";
                res.status(result).send(message)
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//edit an existing task in the tdl
app.put("/tdl/:taskid", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            tdl.editTDL(result.userid, req.params['taskid'], req.body.lat, req.body.lng, req.body.task, req.body.time, req.body.date).then(result => {
                var message;
                if (result == 404) message = "User not found\n";
                else if (result == 405) message = "Task not exist\n";
                else message = "TDL edited successfully\n";
                res.status(result).send(message)
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//add new task to the tdl
app.put("/user/score", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            info.editScore(result.userid, req.body.score).then(response => {
                if (response == 404) res.status(404).send("User not found")
                else {
                    leaderboard.scoreUpdate(result.userid, req.body.score).then(result => {
                        res.status(200).send("Score edited successfully\n")
                    })
                }
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//add new task to the tdl
app.put("/allboards", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            leaderboard.updateAllBoard(req.body.users).then(response => {
                res.status(200).send("Score edited successfully\n")
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//edit user status
app.put("/user/gender", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            info.editGender(result.userid, req.body.gender).then(result => {
                if (result == 404) res.status(404).send("User not found")
                else res.status(200).send("Gender edited successfully\n")
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//edit user status
app.put("/user/age", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            info.editAge(result.userid, req.body.age).then(result => {
                if (result == 404) res.status(404).send("User not found")
                else res.status(200).send("Gender edited successfully\n")
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//edit user status
app.put("/user/status", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            info.editStatus(result.userid, req.body.status).then(result => {
                if (result == 404) res.status(404).send("User not found")
                else res.status(200).send("Status edited successfully\n")
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//add new task to the tdl
app.put("/user/token", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            info.editToken(result.userid, req.body.token).then(result => {
                if (result == 404) res.status(404).send("User not found")
                else res.status(200).send("Token edited successfully\n")
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//get task to tdl at the given location
app.get('/tdl', (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            tdl.getTDL(result.userid).then(result => {
                if (result == 404) res.status(404).send("User not exist");
                else if (result == 201) res.status(201).send(JSON.stringify({"tasklist": []}));
                else res.status(200).send(JSON.stringify({
                    "tasklist": result
                }));
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//delete task from the tdl
app.delete("/tdl/:taskid", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            tdl.deleteTDL(result.userid, req.params['taskid']).then(result => {
                var message;
                if (result == 404) message = "User not found\n";
                else if (result == 405) message = "Task not exist\n";
                else message = "TDL deleted successfully\n";
                res.status(result).send(message)
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//add new location
app.post("/friend/:email", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            friend.addFriend(result.userid, req.params['email'], req.body.name, req.body.friendId).then(response => {
                if (response == 404) res.status(404).send("User not found\n")
                else if (response == 201) res.status(201).send("already friend\n")
                else {
                    Promise.all([leaderboard.addToFriendBoard(result.userid, req.body.friendId), leaderboard.addToFriendBoard(req.body.friendId, result.userid)])
                        .then(response => {
                            res.status(200).send("Friend added successfully\n")
                        })
                }
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

app.get("/friend", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            friend.getFriendList(result.userid).then(result => {
                if (result == 404) res.status(404).send("User not exist")
                else if (result == 201) res.status(201).send(JSON.stringify({"friendlist": []}))
                else res.status(200).send(JSON.stringify(result))
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

app.get("/friend/:email", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            friend.getFriend(result.userid, req.params['email']).then(result => {
                if (result == 404) res.status(404).send("User not exist")
                else res.status(200).send(JSON.stringify(result))
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

app.delete("/friend/:email", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            friend.deleteFriend(result.userid, req.params['email']).then(response => {
                if (response == 404) res.status(response).send("User not found\n")
                else if (response == 405) res.status(response).send("Friend not exist\n")
                else {
                    Promise.all([leaderboard.removeFromFriendBoard(result.userid, response.friendId), leaderboard.removeFromFriendBoard(response.friendId, result.userid)])
                        .then(response => {
                            res.status(200).send("Friend deleted successfully\n")
                        })
                }
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});

//send push notification
app.post("/pn", async (req, res) => {
    verify(req.headers['authorization'])
        .then((result) => {
            info.pn(result.userid, req.body.email).then(result => {
                var message;
                if (result == 404) message = "User not found\n";
                else if (result == 405) message = "Friend not exist\n";
                else message = "PN sent successfully\n";
                res.status(result).send(message)
            }).catch(err => {
                res.status(400).send(err)
            })
        })
        .catch(console.error);
});


// // Auto deploy server upon new commit 
// app.post('/deploy', (req, res) => {   
//     console.log("Deploy starts.");    
//     res.send("Deployment process starts!");
//     // Neglect local changes to avoid conflict during git pull
//     exec("git reset --hard 4fc4db90c0b85bd8455746a27eb6282fd3f3f654", (error, stdout, stderr) => {
//         if (error) {
//             console.log(`error: ${error.message}`);
//         } else if (stderr) {
//             console.log(`stderr: ${stderr}`);
//         } else {
//             console.log(`stdout: ${stdout}`);
//         }
//     });
//     // Wait for 5 seconds to ensure that the new commit is uploaded to GitHub
//     setTimeout(() => {
//         console.log("Pull");
//         exec("git pull | npm install", (error, stdout, stderr) => {
//             if (error) {
//                 console.log(`error: ${error.message}`);
//             } else if (stderr) {
//                 console.log(`stderr: ${stderr}`);
//             } else {
//                 console.log(`stdout: ${stdout}`);
//             }
//             console.log("Restart pm2");
//             exec("pm2 restart test_1", (error, stdout, stderr) => {
//                 if (error) {
//                     console.log(`error: ${error.message}`);
//                     return;
//                 }
//                 if (stderr) {
//                     console.log(`stderr: ${stderr}`);
//                     return;
//                 }
//                 console.log(`stdout: ${stdout}`);  
//             });
//         });   
//     }, 5000);

// });
module.exports = {
    app,
    db
};