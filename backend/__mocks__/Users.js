

function Users() {
    if (!(this instanceof Users)) return new Users();
    this.tdl = [];
    this.friends = [];
    this.addTDL = function (userid, id, lat, lng, task, time, date) {
        return new Promise((resolve, reject) => {
            if (userid === 123456) resolve(0);
            else if (this.tdl.length !== 0 && this.tdl[0] == id) resolve(1)
            else {
                this.tdl.push(id);
                resolve(2)
            }
        })
    }
    this.getTDL = function (userid) {
        return new Promise((resolve, reject) => {
            if (userid == 123456) resolve(0);
            else {
                if (this.tdl.length === 0) resolve(1)
                else resolve(this.tdl)
            }
        })
    }
    this.deleteTDL = function (userid, taskid) {
        return new Promise((resolve, reject) => {
            if (userid == 123456) resolve(0);
            else if (this.tdl.length == 0) resolve(1)
            else {
                this.tdl = [];
                resolve(2)
            }
        })
    }
    this.editTDL = function (userid, id, lat, lng, task, time, date) {
        return new Promise((resolve, reject) => {
            if (userid == 123456) resolve(0);
            else if (this.tdl.length == 0) resolve(1)
            else {
                resolve(2)
            }
        })
    }
    this.getFriendList = function (userid) {
        return new Promise((resolve, reject) => {
            if (userid == 123456) resolve(0);
            else {
                if (this.friends.length == 0) resolve(1)
                else resolve(this.friends)
            }
        })
    }

    this.getFriend = function (userid, email) {
        return new Promise((resolve, reject) => {
            if (userid == 123456) resolve(0);
            else {
                if (this.friends.length === 0) resolve(0)
                else if (email == "friendsss@gmail.com") resolve(0)
                else resolve(this.friends[0])
            }
        })
    }

    this.addFriend = function (userid, email, name, friendId) {
        return new Promise((resolve, reject) => {
            if (userid == 123456) resolve(0);
            else if (friendId == "222223") resolve(0)
            else {
                if (this.friends.length === 0) {
                    this.friends.push({ userid, email, name, friendId })
                    resolve(1)
                }
                else resolve(1)
            }
        })
    }
    this.deleteFriend = function (userid, email) {
        return new Promise((resolve, reject) => {
            if (userid == 123456) resolve(0);
            else {
                if (this.friends.length == 0) resolve(1)
                else {
                    this.friends = []
                    resolve(2)
                }
            }
        })
    }
}

module.exports = Users;