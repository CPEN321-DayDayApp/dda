const express = require('express');
const Database = require('./Database');
const TodoList = require("./todoList");
//const { exec } = require("child_process");
const fs = require('fs')
const {OAuth2Client} = require('google-auth-library');

var CLIENT_ID1 = '';
var CLIENT_ID2 = '';
var CLIENT_ID3 = '';
try {
    if (fs.existsSync('./local_info.js')) {
        const CLIENT_ID = require('./local_info');
        CLIENT_ID1 = CLIENT_ID.CLIENT_ID1;
        CLIENT_ID2 = CLIENT_ID.CLIENT_ID2;
        CLIENT_ID3 = CLIENT_ID.CLIENT_ID3;
    }
  } catch(err) {
    console.error(err)
}

const client = new OAuth2Client(CLIENT_ID1);

const DB_URL = "mongodb://localhost:27017";
const DB_NAME = "data"

const app = express();
const db = new Database(DB_URL, DB_NAME)
const tdl = new TodoList(db)

async function verify(token) {
    const ticket = await client.verifyIdToken({
        idToken: token,
        audience: [CLIENT_ID1,CLIENT_ID2,CLIENT_ID3],  // Specify the CLIENT_ID of the app that accesses the backend
        // Or, if multiple clients access the backend:
        //[CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]
    });
    const payload = ticket.getPayload();
    const userid = payload['sub'];
    const useremail = payload['email'];
    const username = payload['name'];
    var res = {userid, "email": useremail, "name": username};
    return res;
    // If request specified a G Suite domain:
    // const domain = payload['hd'];
  }
// app.use(express.json());
app.use(express.json({limit:'50mb'}))

app.get('/', (req, res) => {
    res.send("Dummy root. Get/Post/Delete to other APIs.");    
});


app.get('/time', (req, res) => {
    res.send((new Date()).toLocaleTimeString());    
});

app.get('/addr', (req, res) => {
    res.send("20.151.160.88");
});


app.get('/test', (req, res) => {
    res.send("This is a test endpoint.");    
});

app.get("/user", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.getUser(result.userid, result.email).then(result =>{
                    res.status(200).send(JSON.stringify(result))
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("User geted unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("User geted unsuccessfully\n")
});

//add new user
app.post("/user", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.addUser(result.userid, result.email, result.name, req.body.token).then(result =>{
                    res.status(200).send("User added successfully\n")
                })
            }
            else{
                console.log(req.headers)
                res.status(400).send("User added unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("User added unsuccessfully\n")
});

app.get("/location", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.getLocation(result.userid).then(result =>{
                    res.status(200).send(JSON.stringify(result))
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Location geted unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Location geted unsuccessfully\n")
});

//add new location
app.post("/location", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.addLocation(result.userid,req.body.lat,req.body.lng).then(result =>{
                    res.status(200).send("Location added successfully\n")
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Location added unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Location added unsuccessfully\n")
});

//delete new location
app.delete("/location", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.deleteLocation(result.userid,req.body.lat,req.body.lng).then(result =>{
                    res.status(200).send("Location deleted successfully\n")
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Location deleted unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Location deleted unsuccessfully\n")
});

//add new task to the tdl
app.post("/tdl", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                tdl.addTDL(result.userid,req.body.taskId,req.body.lat,req.body.lng,req.body.task,req.body.time,req.body.date).then(result =>{
                    res.status(200).send("TDL added successfully\n")
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("TDL added unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("TDL added unsuccessfully\n")
});

//edit an existing task in the tdl
app.put("/tdl/:taskid", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                tdl.editTDL(result.userid,req.params['taskid'],req.body.lat,req.body.lng,req.body.task,req.body.time,req.body.date).then(result =>{
                    res.status(200).send("TDL edited successfully\n")
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("TDL edited unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("TDL edited unsuccessfully\n")   
});

//add new task to the tdl
app.put("/user/score", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.editScore(result.userid, req.body.score).then(result =>{
                    res.status(200).send("Score edited successfully\n")
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Score edited unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Score edited unsuccessfully\n")
});

//edit user status
app.put("/user/status", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.editStatus(result.userid, req.body.status).then(result =>{
                    res.status(200).send("Status edited successfully\n")
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Status edited unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Status edited unsuccessfully\n")
});

//add new task to the tdl
app.put("/user/token", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.editToken(result.userid, req.body.token).then(result =>{
                    res.status(200).send("Token edited successfully\n")
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Token edited unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Token edited unsuccessfully\n")
});

//get task to tdl at the given location
app.get('/tdl', (req, res) => {
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                tdl.getTDL(result.userid).then(result =>{
                    res.status(200).send(JSON.stringify({"tasklist":result}))
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("TDL geted unsuccessfully\n")
            } 
        })
        .catch(console.error);
    }
    else res.status(400).send("TDL geted unsuccessfully\n")
});

//delete task from the tdl
app.delete("/tdl/:taskid", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                tdl.deleteTDL(result.userid, req.params['taskid']).then(result =>{
                    res.status(200).send("Task deleted successfully\n")
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Task deleted unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Task deleted unsuccessfully\n")
});

//add new location
app.post("/friend/:email", async (req,res)=>{
    if(!(/\S[^\s@]*@\S+\.\S+/.test(req.params['email'])))
        return res.status(400).send("Invalid email")
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.addFriend(result.userid,req.params['email'],req.body.name,req.body.friendId).then(response =>{
                    db.addFriend(req.body.friendId, result.email,result.name,result.userid).then(result=>{
                        res.status(200).send("Friend added successfully")
                    })
                    .catch(err =>{
                        res.status(400).send(err)
                    })
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Friend added unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Friend added unsuccessfully\n")
});

app.get("/friend", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.getFriendList(result.userid).then(result =>{
                    res.status(200).send(JSON.stringify(result))
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Friendlist geted unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Friendlist geted unsuccessfully\n")
        
});

app.get("/friend/:email", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.getFriend(result.userid,req.params['email']).then(result =>{
                    res.status(200).send(JSON.stringify(result))
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Friend geted unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Friend geted unsuccessfully\n")
});

app.delete("/friend/:email", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            if(result.userid){
                db.deleteFriend(result.userid,req.params['email']).then(result =>{
                    db.deleteFriend(result.friendId, result.email).then(result=>{
                        res.status(200).send("Friend deleted successfully")
                    })
                    .catch(err =>{
                        res.status(400).send(err)
                    })
                }).catch(err =>{
                    res.status(400).send(err)
                })
            }
            else{
                res.status(400).send("Friend deleted unsuccessfully\n")
            }
        })
        .catch(console.error);
    }
    else res.status(400).send("Friend deleted unsuccessfully\n")
});

//send push notification
app.post("/pn", async (req,res)=>{
    if(req.headers['authorization']){
        verify(req.headers['authorization'])
        .then((result)=>{
            db.pn(result.userid,req.body.email).then(result =>{
                res.status(200).send("Push notification sent\n")
            })
        })
        .catch(console.error);
    }
    else res.status(400).send("Notification pushed unsuccessfully\n")
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
module.exports = {app,db};