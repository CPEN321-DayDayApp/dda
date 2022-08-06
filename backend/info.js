
function Info(user) {
    this.addLocation = function (userid, lat, lng) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number" && !isNaN(lat) && !isNaN(lng)) {
                user.addLocation(userid, lat, lng).then(result => {
                    if (result === 0) resolve(404)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }
    this.getLocation = function (userid) {
        return new Promise((resolve, reject) => {
                user.getLocation(userid).then(result=>{
                    if(result===0) resolve(404)
                    else resolve(result)
                })
            })
    }
    this.deleteLocation = function (userid, lat, lng) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number" && !isNaN(lat) && !isNaN(lng)) {
                user.deleteLocation(userid, lat, lng).then(result => {
                    if (result === 0) resolve(404)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }
    this.pn = function (userid, email) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number" && (/\S[^\s@]*@\S+\.\S+/.test(email))) {
                user.pn(userid, email).then(result => {
                    if (result === 0) resolve(404)
                    else if (result === 1) resolve(405)
                    else if (result === 2) resolve(201)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }
    this.editScore = function (userid, score) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number" && !isNaN(score)) {
                user.editScore(userid, score).then(result => {
                    if (result === 0) resolve(404)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }

    this.editToken = function (userid, token) {
        return new Promise((resolve, reject) => {
            user.editToken(userid, token).then(result=>{
                if(result==0) resolve(404)
                else resolve(200)
            })
        })
    }

    this.editGender = function (userid, gender) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number") {
                user.editGender(userid, gender).then(result => {
                    if (result === 0) resolve(404)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }

    this.editAge = function (userid, age) {
        return new Promise((resolve, reject) => {
            if (!isNaN(userid) && typeof (userid) != "number" && !isNaN(age)) {
                user.editAge(userid, age).then(result => {
                    if (result === 0) resolve(404)
                    else resolve(200)
                })
            }
            else {
                reject("Invalid Input")
            }
        })
    }

    this.editStatus = function (userid, status) {
        return new Promise((resolve, reject) => {
            user.editStatus(userid, status).then(result => {
                if (result === 0) resolve(404)
                else resolve(200)
            })
        })
    }
}

module.exports = Info;