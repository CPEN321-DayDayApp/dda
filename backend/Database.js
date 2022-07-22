const { MongoClient } = require('mongodb');   // require the mongodb driver
var admin = require("firebase-admin");
var serviceAccount = require("./daydayapp_keyprivate.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });
  
const payload ={
    notification:{
        title: 'Time to Work',
        body: 'Your friend reminds you to study'
    },
    data:{
        data1: 'if we need something to send'
    }
}

const options = {priority: 'high', timeToLive: 60*60*24}

function Database(mongoUrl, dbName){
    if (!(this instanceof Database)) return new Database(mongoUrl, dbName);
    this.connected = new Promise((resolve, reject) => {
        MongoClient.connect(
            mongoUrl,
            {
                useNewUrlParser: true
            },
            (err, client) => {
                if (err) reject(err);
                else {
                    console.log('[MongoClient] Connected to ' + mongoUrl + '/' + dbName);
                    resolve(client.db(dbName));
                }
            }
        )
    });
    this.status = () => this.connected.then(
        db => ({ error: null, url: mongoUrl, db: dbName }),
        err => ({ error: err })
    );
}
Database.prototype.pn = function(userid, email){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid).findOne({ 
                _id: "userinfo",
                friendlist: {
                    $elemMatch: {
                        email
                    }
              } })
            .then(result=>{
                if(result==null) reject("no such friend")
                result.friendlist.forEach(element => {
                    if(element.email==email){
                        db.collection(element.friendId).findOne({_id:"userinfo"})
                        .then(result=>{
                            admin.messaging().sendToDevice(result.token,payload,options).then(result =>{
                                resolve(result)}, err=>{reject(err)})
                        }, err =>{reject(err)})
                    }
                })
                resolve(result)
            }, err=>{reject(err)})
        })
    )
}
Database.prototype.addUser = function(userid, email, name, token){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.listCollections().toArray(function(err, collinfos) {
                if(err) reject(err);
                collinfos.forEach(collinfo => {
                   if(collinfo['name']==userid){
                       resolve("user already exists")
                   }
                })
                db.collection(userid).insertOne({'_id': "userinfo", token, email, name, 'score':0, 'status': false, 'location':[], 'friendlist': []})
                .then((result) => { resolve(result); }, (err) => { reject(err); })
            })
            
        })
    )
}

Database.prototype.getUser = function(userid, email){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid)
                .findOne({email})
                .then((result) => { resolve({'name':result.name,'score':result.score, 'rank': 0, 'status': result.status, 'token': result.token}); }, (err) => { reject(err); });
        })
    )
}

Database.prototype.addLocation = function(userid, lat, lng){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid).updateOne(
                { _id: "userinfo" },
                { $addToSet: { location: {lat, lng} } }
             )
            .then((result) => { resolve(result);}, (err) => { reject(err); });
        })
    )
}

Database.prototype.getFriendList = function(userid){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid).findOne({ _id: "userinfo" })
            .then((result) => { resolve({"friendlist":result.friendlist});}, (err) => { reject(err); });
        })
    )
}

Database.prototype.getFriend = function(userid, email){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.listCollections().toArray(function (err, collinfos) {
                if(err) reject(err);
                var index=0;
                collinfos.forEach(collinfo => {
                    db.collection(collinfo['name']).findOne({email})
                    .then((result)=>{
                            index++;
                            if(result!=null){
                                resolve({"name": result.name, "friendId": collinfo['name']})
                            }
                            if(index===collinfos.length-1) reject("No such user")
                        },(err) => { reject(err); })
                })
            })
        })
    )
}

Database.prototype.addFriend = function(userid, email, name, friendId){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            this.getFriend(userid,email).then(()=>{
                db.collection(userid).findOne({
                    _id:"userinfo",
                    friendlist: {
                      $elemMatch: {
                        email
                      }
                    }
                  })
                  .then((result)=>{
                    if(result == null){
                    db.collection(friendId).findOne({_id:"userinfo" })
                    .then(result=>{
                        db.collection(userid).updateOne(
                            { _id: "userinfo" },
                            { $addToSet: { friendlist: {friendId, "name": result.name, "email": result.email, "status": result.status} } }
                        )
                        .then((result) => { resolve(result);}, 
                            (err) => { reject(err); });
                    },(err)=>{reject(err)})
                    }
                    else{
                        reject("already friend")
                    }
                  },(err)=>{reject(err)})
            }, err=>reject(err))
        })
    )
}

Database.prototype.getLocation = function(userid){
return this.connected.then(db =>
    new Promise((resolve, reject) => {
        db.collection(userid).findOne({ _id: "userinfo" })
        .then((result) => { resolve({"location":result.location});}, (err) => { reject(err); });
    })
)
}

Database.prototype.addTDL = function(userid, _id, lat, lng, task, time, date){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid).insertOne({_id, lat, lng, task, time, date})
                .then((result) => { resolve(result); }, (err) => { reject(err); });
        })
    )
}
Database.prototype.getTDL = function(userid){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid)
                .find( { time: { $gt: 0 } } )
                .toArray((err, result)=>{
                    if(err) reject(err)
                    else{
                        resolve(result)
                    }
                }
                )
        })
    )
}

Database.prototype.deleteFriend = function(userid, email){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid).findOne({
                _id:"userinfo",
                friendlist: {
                  $elemMatch: {
                    email
                  }
                }
              })
              .then((result)=>{
                if(result != null){
                    var id;
                    result.friendlist.forEach(element =>{
                        if(element.email==email){
                            id=element.friendId;
                        }
                    })
                    db.collection(userid)
                    .updateOne(
                    { _id: "userinfo" },
                    { $pull: { friendlist: {email} } }
                )
                .then((response) => { resolve({"friendId":id,"email":result.email}); }, (err) => { reject(err); });
                }
                else reject("already not friend")
              })
        })
    )
}

Database.prototype.deleteLocation = function(userid, lat, lng){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid)
            .updateOne(
                { _id: "userinfo" },
                { $pull: { location: {lat, lng} } }
             )
                .then((result) => { resolve(result); }, (err) => { reject(err); });
        })
    )
}

Database.prototype.deleteTDL = function(userid,taskid){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid)
                .deleteOne({'_id': parseInt(taskid,10)})
                .then((result) => { resolve(result); }, (err) => { reject(err); });
        })
    )
}

Database.prototype.editTDL = function(userid, id, lat, lng, task, time, date){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid)
                .updateOne(
                    { _id: parseInt(id,10) },
                    { $set: { lat, lng, task, time, date} }
                )
                .then((result) => { resolve(result); }, (err) => { reject(err); });
        })
    )
}

Database.prototype.editScore = function(userid, score){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid)
                .updateOne(
                    { _id: "userinfo" },
                    { $set: {score} }
                )
                .then((result) => { resolve(result); }, (err) => { reject(err); });
        })
    )
}

Database.prototype.editToken = function(userid, token){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid)
                .updateOne(
                    { _id: "userinfo" },
                    { $set: {token} }
                )
                .then((result) => { resolve(result); }, (err) => { reject(err); });
        })
    )
}

Database.prototype.editStatus = function(userid, status){
    return this.connected.then(db =>
        new Promise((resolve, reject) => {
            db.collection(userid)
                .updateOne(
                    { _id: "userinfo" },
                    { $set: {status} }
                )
                .then((result) => { 
                    db.collection(userid).findOne({ _id: "userinfo" })
                    .then(result=>{
                        result.friendlist.forEach(element => {
                            db.collection(element.friendId).updateOne(
                                { _id: "userinfo", "friendlist.friendId": userid},
                                { $set:  { "friendlist.$.status" : status} }
                            )
                        })
                        resolve(result)
                    }, err=>{reject(err)})
                });
        })
    )
}
module.exports = Database;