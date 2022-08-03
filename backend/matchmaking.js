function MatchMaking(db){
    this.match = {
        1: {
            player1: undefined,
            player2: undefined,
        },
        2: {
            player1: undefined,
            player2: undefined,
        },
        3: {
            player1: undefined,
            player2: undefined,
        },
        4: {
            player1: undefined,
            player2: undefined,
        },
        5: {
            player1: undefined,
            player2: undefined,
        }
    }
    this.getOpponent = function(userid, level){
        return db.connected.then(db =>
            new Promise((resolve, reject) => {
                db.listCollections().toArray(function(err, collinfos) {
                    if(err) reject(err);
                    var num=0;
                    if(collinfos.length==0) num=1
                    collinfos.forEach(collinfo => {
                        num++;
                       if(collinfo['name']==userid){
                           resolve("user already exists")
                       }
                    })
                    db.collection(userid).insertOne({'_id': userid, name, score,"rank":1})
                    .then((result) => { 
                        db.collection("globalboard").insertOne({'_id': userid, name, score,"rank":num})
                        .then((result) => { 
                            resolve(result);
                        }, (err) => { reject(err); })
                    }, (err) => { reject(err); })
                })
                
            })
        )
    }
    
}

module.exports = MatchMaking;