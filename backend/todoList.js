

function TodoList(db){
    if (!(this instanceof TodoList)) return new TodoList();
    this.addTDL = function(userid, id, lat, lng, task, time, date){
        return new Promise((resolve,reject)=>{
            if(!isNaN(userid)&&!isNaN(id)&&!isNaN(time)&&!isNaN(lat)&&!isNaN(lng)&&task!=null&&isNaN(date)){
                db.addTDL(userid, id, lat, lng, task, time, date).then(result =>{
                    resolve(result)
                }).catch(err =>{
                    reject(err)
                })
            }
            else{
                reject("Invalid Input")
            }
        })
    }
    this.getTDL = function(userid){
        return new Promise((resolve, reject) => {
                if(!isNaN(userid)){
                    db.getTDL(userid).then(result =>{
                        resolve(result)
                    }).catch(err =>{
                        reject(err)
                    })
                }
                else{
                    reject("Invalid Input")
                }
            })
    }
    this.deleteTDL = function(userid,taskid){
        return new Promise((resolve, reject) => {
                if(!isNaN(userid)&&!isNaN(taskid)){
                    db.deleteTDL(userid,taskid).then(result =>{
                        resolve(result)
                    }).catch(err =>{
                        reject(err)
                    })
                }
                else{
                    reject("Invalid Input")
                }
            })
    }
    this.editTDL = function(userid, id, lat, lng, task, time, date){
        return new Promise((resolve, reject) => {
                if(!isNaN(userid)&&!isNaN(id)&&!isNaN(time)&&!isNaN(lat)&&!isNaN(lng)&&task!=null&&isNaN(date)){
                    db.editTDL(userid, id, lat, lng, task, time, date).then(result =>{
                        resolve(result)
                    }).catch(err =>{
                        reject(err)
                    })
                }
                else{
                    reject("Invalid Input")
                }
            })
    }
}

module.exports = TodoList;