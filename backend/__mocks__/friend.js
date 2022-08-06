

function Friend(db){
    if (!(this instanceof TodoList)) return new TodoList();
    this.addFriend = function(userid, id, lat, lng, task, time, date){
        return Promise.resolve({"acknowledged" : true, "insertedId" : id})
    }
    this.getFriend = function(userid){
        return Promise.resolve({"taskId": 123456, "lat": 11.1, "lng": 12.2, "task":"Something", "time":30, "date":"Jan 11st"})
    }
    this.deleteFriend = function(userid,taskid){
        return Promise.resolve({ "acknowledged" : true, "deletedCount" : 1 })
    }
    this.getFriendList = function(userid, id, lat, lng, task, time, date){
        return Promise.resolve({ "acknowledged" : true, "matchedCount" : 1, "modifiedCount" : 1 })
    }
}

module.exports = Friend;