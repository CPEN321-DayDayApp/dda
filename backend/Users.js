

function Users(db){
    this.userExist = function(userid){
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.listCollections().toArray(function(err, collinfos) {
                    if(err) reject(err);
                    var index=0;
                    collinfos.forEach(collinfo => {
                        index++;
                       if(collinfo['name']===userid){
                           resolve(1)
                       }
                       if(index===collinfos.length) resolve(0)
                    })
                })
                
            })
        )
    }
    this.friendExist= function(userid, friendid){
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.listCollections().toArray(function(err, collinfos) {
                    if(err) reject(err);
                    var index=0;
                    var tmp=2;
                    collinfos.forEach(collinfo => {
                        index++;
                       if(collinfo['name']===userid || collinfo['name']===friendid){
                           tmp--;
                       }
                       if(tmp===0) resolve(1)
                       if(index===collinfos.length) resolve(0)
                    })
                })
                
            })
        )
    }
    this.tdlExist = function(userid,_id){
        return db.connected.then(database =>
            new Promise((resolve, reject) => {
                this.userExist(userid).then(result=>{
                    if(result==0) resolve(0)
                    else{
                        database.collection(userid).findOne({ _id})
                            .then((result)=>{
                            if(result!=null){
                                resolve(1);
                            }
                            else{
                                resolve(2);
                            }
                        })
                    }
                })
            }
        ))
    }
    this.addTDL = function(userid, id, lat, lng, task, time, date){
        return new Promise((resolve,reject)=>{
            this.tdlExist(userid,id).then(result=>{
                if(result==0) resolve(0)
                else if(result==1) resolve(1)
                else{
                    db.addTDL(userid, id, lat, lng, task, time, date).then(result =>{
                        resolve(result)
                    })
                }
            })
        })
    }
    this.getTDL = function(userid){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.getTDL(userid).then(result =>{
                        resolve(result)
                    })
                }
            })
        })
    }
    this.deleteTDL = function(userid,taskid){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.deleteTDL(userid,taskid).then(result =>{
                        if(result["deletedCount"]==0) resolve(1)
                        else resolve(2)
                    })
                }
            })
        })
    }
    this.editTDL = function(userid, id, lat, lng, task, time, date){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.editTDL(userid, id, lat, lng, task, time, date).then(result =>{
                        if(result["matchedCount"]==0) resolve(1)
                        else resolve(result)
                    })
                }
            })
        })
    }
    this.getFriendList = function(userid){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.getFriendList(userid).then(result =>{
                        resolve(result)
                    })
                }
            })
        })
    }
    
    this.getFriend = function(userid, email){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.getFriend(userid,email).then(result =>{
                        resolve(result)
                    })
                }
            })
        })
    }
    
    this.addFriend = function(userid, email, name, friendId){
        return new Promise((resolve, reject) => {
            this.friendExist(userid,friendId).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.addFriend(userid,email,name,friendId).then(response =>{
                        db.addFriend(friendId, email,name,userid).then(result=>{
                            resolve(1)
                        })
                    })
                }
            })
        })
    }
    this.deleteFriend = function(userid, email){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.deleteFriend(userid,email).then(result =>{
                        if(result=="already not friend") resolve(1)
                        else db.deleteFriend(result.friendId, result.email).then(result=>{
                            resolve(2)
                        })
                    })
                }
            })
        })
    }
    this.addLocation = function(userid, lat, lng){
        return new Promise((resolve, reject) => {
                this.userExist(userid).then(result=>{
                    if(result==0) resolve(0)
                    else{
                        db.addLocation(userid, lat, lng).then(result =>{
                            resolve(result)
                        })
                    }
                })
            })
    }
    this.getLocation = function(userid){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.getLocation(userid).then(result =>{
                        resolve(result)
                    })
                }
            })
        })
    }
    this.deleteLocation = function(userid, lat, lng){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.deleteLocation(userid, lat, lng).then(result =>{
                        resolve(result)
                    })
                }
            })
        })
    }
    this.pn = function(userid, email){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.pn(userid, email).then(result=>{
                        if(result=="no such friend") resolve(1)
                        else if(result==1) resolve(2)
                        else resolve(3)
                    })
                }
            })
        })
    }

    this.editScore = function(userid, score){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.editScore(userid, score).then(result =>{
                        resolve(result)
                    })
                }
            })
        })
    }
    
    this.editToken = function(userid, token){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.editToken(userid, token).then(result =>{
                        resolve(result)
                    })
                }
            })
        })
    }
    
    this.editStatus = function(userid, status){
        return new Promise((resolve, reject) => {
            this.userExist(userid).then(result=>{
                if(result==0) resolve(0)
                else{
                    db.editStatus(userid, status).then(result =>{
                        resolve(result)
                    })
                }
            })
        })
    }
}

module.exports = Users;