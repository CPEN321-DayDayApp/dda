
function TodoList(user) {
    this.addTDL = function (userid, id, lat, lng, task, time, date) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number" && !isNaN(id) && !isNaN(time) && !isNaN(lat) && !isNaN(lng) && task != null && isNaN(date)) {
                user.addTDL(userid, id, lat, lng, task, time, date).then(result => {
                    if (result === 0) resolve(404)
                    else if (result === 1) resolve(405)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }
    this.getTDL = function (userid) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number") {
                user.getTDL(userid).then(result => {
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
    this.deleteTDL = function (userid, taskid) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number" && !isNaN(taskid)) {
                user.deleteTDL(userid, taskid).then(result => {
                    if (result === 0) resolve(404)
                    else if (result === 1) resolve(405)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }
    this.editTDL = function (userid, id, lat, lng, task, time, date) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number" && !isNaN(id) && !isNaN(time) && !isNaN(lat) && !isNaN(lng) && task != null && isNaN(date)) {
                user.editTDL(userid, id, lat, lng, task, time, date).then(result => {
                    if (result === 0) resolve(404)
                    else if (result === 1) resolve(405)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }
}

module.exports = TodoList;