

function LeaderBoard(db) {
    const that = this;
    this.scoreUpdateHelper = function (collname) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.collection(collname).aggregate([
                    {
                        $setWindowFields: {
                            sortBy: { score: -1 },
                            output: {
                                rank: {
                                    $rank: {}
                                }
                            },
                        }
                    },
                    { $merge: collname }
                ]).toArray()
                resolve(1)
            })
        )
    }

    this.addToFriendBoard = function (userid, friendid) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.collection(friendid).findOne({ _id: friendid }).then(result => {
                    db.collection(userid).insertOne(result).then(result => {
                        this.scoreUpdateHelper(userid).then(values => {
                            resolve(result)
                        })
                    })
                })
            })
        )
    }
    this.removeFromFriendBoard = function (userid, friendid) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.collection(userid).deleteOne({ _id: friendid }).then(result => {
                    this.scoreUpdateHelper(userid).then(values => {
                        resolve(result)
                    })
                })
            })
        )
    }

    // without competition
    // this.scoreUpdateGlobal= function(userid, score){
    //     return db.connected.then(db =>
    //         new Promise((resolve, reject) => {
    //             db.collection("globalboard").updateOne(
    //                 { _id: userid },
    //                 { $set: {score} }
    //             )
    //             .then((result) => { 
    //                 this.scoreUpdateHelper("globalboard").then(result=>{
    //                     resolve(result);
    //                 })
    //              }, (err) => { reject(err); });
    //         })
    //     )
    // }
    // this.scoreUpdateFriend= function(userid, score){
    //     return db.connected.then(db =>
    //         new Promise((resolve, reject) => {
    //             db.collection(userid).find({}).toArray((err,result)=>{
    //                 var num=0;
    //                 result.forEach(document=>{
    //                     db.collection(document['_id']).updateOne(
    //                         { _id: userid },
    //                         { $set: {score} }
    //                     ).then(response=>{
    //                         this.scoreUpdateHelper(document['_id']).then(response=>{
    //                             num++
    //                             if(num==result.length) resolve(200);
    //                         })
    //                     })
    //                 })
    //             })
    //         })
    //     )
    // }
    // this.scoreUpdate= function(userid, score){
    //     return db.connected.then(db =>
    //         new Promise((resolve, reject) => {
    //             var p=[];
    //             p.push(this.scoreUpdateGlobal(userid, score));
    //             p.push(this.scoreUpdateFriend(userid, score));
    //             Promise.all(p).then(values=>resolve(200))
    //         })
    //     )
    // }

    this.scoreUpdateGlobal = function (userid, score) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.collection("globalboard").updateOne(
                    { _id: userid },
                    { $set: { score } }
                )
                    .then((result) => {
                        resolve(result);
                    }, (err) => { reject(err); });
            })
        )
    }
    this.scoreUpdateFriend = function (userid, score) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.collection(userid).find({}).toArray((err, result) => {
                    if(err) reject(err)
                    var num = 0;
                    result.forEach(document => {
                        db.collection(document['_id']).updateOne(
                            { _id: userid },
                            { $set: { score } }
                        ).then(response => {
                            num++
                            if (num == result.length) resolve(200);
                        })
                    })
                })
            })
        )
    }
    this.scoreUpdate = function (users) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                var num = 0;
                users.forEach(user => {
                    Promise.all([this.scoreUpdateGlobal(user.userid, user.score), this.scoreUpdateFriend(user.userid, user.score)])
                        .then(values => {
                            num++;
                            if (num == users.length) resolve(1)
                        })
                })
            })
        )
    }

    this.updateFriendBoard = function (users) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                var num = 0
                users.forEach(user => {
                    this.scoreUpdateHelper(user.userid)
                        .then(value => {
                            num++;
                            if (num == users.length) resolve(1)
                        })
                })
            })
        )
    }
    this.updateGlobalBoard = function () {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                this.scoreUpdateHelper("globalboard")
                    .then(value => {
                        resolve(1)
                    })
            })
        )
    }

    this.updateAllBoard = function (users) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                if (users.length === 0)
                    resolve(200)
                else this.scoreUpdate(users).then(result => {
                        Promise.all([this.updateFriendBoard(users), this.updateGlobalBoard()])
                            .then(values => {
                                resolve(200)
                            })
                    })
            })
        )
    }

    this.getFriendBoard = function (userid) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.collection(userid)
                    .find({})
                    .sort({ rank: 1 })
                    .toArray((err, result) => {
                        if (err) reject(err)
                        else {
                            if (result.length === 0) resolve(201)
                            else {
                                resolve({ "friendboard": result })
                            }
                        }
                    }
                    )
            })
        )
    }
    this.getGlobalBoard = function () {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.collection("globalboard")
                    .find({})
                    .sort({ rank: 1 })
                    .toArray((err, result) => {
                        if (err) reject(err)
                        else {
                            if (result.length === 0) resolve(201)
                            else {
                                resolve({ "globalboard": result })
                            }
                        }
                    }
                    )
            })
        )
    }
    this.getGlobalRank = function (userid) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.collection("globalboard")
                    .findOne({ _id: userid })
                    .then(result => {
                        resolve({ "globalrank": result.rank })
                    })
            })
        )
    }
    this.getFriendRank = function (userid) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.collection(userid)
                    .findOne({ _id: userid })
                    .then(result => {
                        resolve({ "friendrank": result.rank })
                    })
            })
        )
    }
    this.newPlayer = function (userid, name, score) {
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.listCollections().toArray(function (err, collinfos) {
                    if (err) reject(err);
                    var num = 0;
                    if (collinfos.length === 0) num = 1
                    collinfos.forEach(collinfo => {
                        num++;
                        if (collinfo['name'] == userid) {
                            resolve("user already exists")
                        }
                    })
                    Promise.all([db.collection(userid).insertOne({ '_id': userid, name, score, "rank": 1 }),
                    db.collection("globalboard").insertOne({ '_id': userid, name, score, "rank": num })])
                        .then((result) => {
                            that.updateGlobalBoard().then(values => {
                                resolve(result)
                            })
                        }, (err) => { reject(err); })
                })

            })
        )
    }
}


module.exports = LeaderBoard;