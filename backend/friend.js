
function Friend(user) {
    this.getFriendList = function (userid) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != Number) {
                user.getFriendList(userid).then(result => {
                    if (result === 0) resolve(404)
                    else if (result === 1) resolve(201)
                    else resolve(result)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }

    this.getFriend = function (userid, email) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != Number && (/\S[^\s@]*@\S+\.\S+/.test(email))) {
                user.getFriend(userid, email).then(result => {
                    if (result === 0) resolve(404)
                    else resolve(result)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }

    this.addFriend = function (userid, email, name, friendId) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != Number && !isNaN(friendId) && (/\S[^\s@]*@\S+\.\S+/.test(email))) {
                user.addFriend(userid, email, name, friendId).then(result => {
                    if (result === 0) resolve(404)
                    else if (result === 1) resolve(201)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }
    this.deleteFriend = function (userid, email) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != Number && (/\S[^\s@]*@\S+\.\S+/.test(email))) {
                user.deleteFriend(userid, email).then(result => {
                    if (result === 0) resolve(404)
                    else if (result === 1) resolve(405)
                    else resolve(result)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }
}

module.exports = Friend;