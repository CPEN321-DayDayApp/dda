const {app,db} = require('./app');
const request = require('supertest');

function MockPayLoad1(){
  this.getPayload=function(){
    return {"sub":"222222","email":"mock@gmail.com","name":"fake name"};
  }
}
function MockPayLoad2(){
  this.getPayload=function(){
    return {"sub":"333333","email":"mock@gmail.com","name":"fake name"};
  }
}
function MockPayLoad3(){
  this.getPayload=function(){
    return {"sub":"invalid","email":"mock@gmail.com","name":"fake name"};
  }
}
function MockFriend(){
  this.getPayload=function(){
    return {"sub":"444444","email":"friend@gmail.com","name":"Billy Yan"};
  }
}
function MockOAuth2Client(){
  this.verifyIdToken=async function(token){
    if(token.idToken=="qbcdefg") return new MockPayLoad1();
    else if(token.idToken=="abcdefg") return new MockPayLoad2();
    else if(token.idToken=="friend") return new MockFriend();
    else return new MockPayLoad3();
  }
}

jest.mock("google-auth-library", ()=>{
  const res = {
    __esModule: true,
    OAuth2Client: jest.fn(()=>{return new MockOAuth2Client();}),
  }
  return res
});
module.exports = () => describe('integration test', () => { 
    beforeAll(async () => {
      connection = await db.connected;
    });
    test("post user, add user", (done) => {
      request(app)
        .post('/user')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"token": 123321})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post user, add again", (done) => {
      request(app)
        .post('/user')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"token": 123321})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post user, add user2", (done) => {
      request(app)
        .post('/user')
        .set('Authorization', "friend")
        .set('Content-Type', 'application/json')
        .send({"token": "f6zMjW6A310:APA91bG7nrXVYj-nCgJrsRTuFE9kMg_vFLdOST77xAtjPeP0uFrZVOe-SP7wmU8i0s3_Y1W8SRCJaD3nxoIB7K0eJOiVTop5DV_f9h3rvdldngE2GpSNqwco98hSXyFAwqVDAYDeePKc"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post user, unseccessful", (done) => {
      request(app)
        .post('/user')
        .set('Authorization', "invalid")
        .set('Content-Type', 'application/json')
        .send({"token": 123321})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get user", (done) => {
      request(app)
        .get('/user')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post task, invalid user", (done) => {
      request(app)
        .post('/tdl')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .send({"taskId":111111,"lat":99.9,"lng":88.8,"task":"BBQ","time":30,"date":"Jan 11st"})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post task: invalid input", (done) => {
      request(app)
        .post('/tdl')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"taskId":111111,"lat":"afasf","lng":"fsbaf","task":"BBQ","time":30,"date":"Jan 11st"})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post task: successful", (done) => {
      request(app)
        .post('/tdl')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"taskId":111111,"lat":99.9,"lng":88.8,"task":"BBQ","time":30,"date":"Jan 11st"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post task: task exist already", (done) => {
      request(app)
        .post('/tdl')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"taskId":111111,"lat":99.9,"lng":88.8,"task":"BBQ","time":30,"date":"Jan 11st"})
        .expect(405)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get task, invalid user", (done) => {
      request(app)
        .get('/tdl')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get task: invalid input", (done) => {
      request(app)
        .get('/tdl')
        .set('Authorization', "invalid")
        .set('Content-Type', 'application/json')
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get task: successful", (done) => {
      request(app)
        .get('/tdl')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("edit task: invalid user", (done) => {
      request(app)
        .put('/tdl/111111')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .send({"lat":99.9,"lng":88.8,"task":"BBQ","time":30,"date":"Jan 11st"})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("edit task: invalid input", (done) => {
      request(app)
        .put('/tdl/111111')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .send({"lat":99.9,"lng":"avs","task":"BBQ","time":30,"date":"Jan 11st"})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("edit task: successful", (done) => {
      request(app)
        .put('/tdl/111111')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"lat":99.9,"lng":88.8,"task":"BBQ","time":30,"date":"Jan 11st"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete task: invalid user", (done) => {
      request(app)
        .delete('/tdl/111111')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete task: invalid input", (done) => {
      request(app)
        .delete('/tdl/afddbfds')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete task: successful", (done) => {
      request(app)
        .delete('/tdl/111111')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete task: task not exist", (done) => {
      request(app)
        .delete('/tdl/111111')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(405)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("edit task: task not exist", (done) => {
      request(app)
        .put('/tdl/111111')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"lat":99.9,"lng":88.8,"task":"BBQ","time":30,"date":"Jan 11st"})
        .expect(405)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get task: task not exist", (done) => {
      request(app)
        .get('/tdl')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(201)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("add friend: invalid user", (done) => {
      request(app)
        .post('/friend/friend@gmail.com')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .send({"name":'Billy Yan',"friendId":"444444"})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("add friend: invalid friend", (done) => {
      request(app)
        .post('/friend/notexist@gmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"name":'Eva Wang',"friendId":"333333"})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("add friend: invalid input", (done) => {
      request(app)
        .post('/friend/notexist@gmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"name":'Billy Yan',"friendId":"notexist"})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("add friend: successful", (done) => {
      request(app)
        .post('/friend/friend@gmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"name":'Billy Yan',"friendId":"444444"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("add friend: already friend", (done) => {
      request(app)
        .post('/friend/friend@gmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"name":'Billy Yan',"friendId":"444444"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get friendlist: invalid input", (done) => {
      request(app)
        .get('/friend')
        .set('Authorization', "invalid")
        .set('Content-Type', 'application/json')
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get friendlist: invalid user", (done) => {
      request(app)
        .get('/friend')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get friendlist: successful", (done) => {
      request(app)
        .get('/friend')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get friend: invalid input", (done) => {
      request(app)
        .get('/friend/friendgmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get friend: invalid user", (done) => {
      request(app)
        .get('/friend/friend@gmail.com')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get friend: invalid friend", (done) => {
      request(app)
        .get('/friend/friendsss@gmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get friend: successful", (done) => {
      request(app)
        .get('/friend/friend@gmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete friend: invalid user", (done) => {
      request(app)
        .delete('/friend/friend@gmail.com')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete friend: invalid input", (done) => {
      request(app)
        .delete('/friend/friend@gmail.com')
        .set('Authorization', "invalid")
        .set('Content-Type', 'application/json')
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete friend: successful", (done) => {
      request(app)
        .delete('/friend/friend@gmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete friend: already deleted", (done) => {
      request(app)
        .delete('/friend/friend@gmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(405)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get friendlist: empty list", (done) => {
      request(app)
        .get('/friend')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(201)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get location: empty location", (done) => {
      request(app)
        .get('/location')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post location: invalid input", (done) => {
      request(app)
        .post('/location')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"lat":'Billy Yan',"lng":"notexist"})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post location: invalid user", (done) => {
      request(app)
        .post('/location')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .send({"lat": 99.9,"lng":90.8})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post location: successful", (done) => {
      request(app)
        .post('/location')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"lat": 99.9,"lng":90.8})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post location: location exists", (done) => {
      request(app)
        .post('/location')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"lat": 99.9,"lng":90.8})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get location: invalid user", (done) => {
      request(app)
        .get('/location')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get location: invalid input", (done) => {
      request(app)
        .get('/location')
        .set('Authorization', "invalid")
        .set('Content-Type', 'application/json')
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("get location: successful", (done) => {
      request(app)
        .get('/location')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete location: invalid user", (done) => {
      request(app)
        .delete('/location')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .set('lat',"99.9")
        .set('lng',"90.8")
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete location: invalid input", (done) => {
      request(app)
        .delete('/location')
        .set('Authorization', "invalid")
        .set('Content-Type', 'application/json')
        .set('lat',"56.4")
        .set('lng',"90.8")
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete location: successful", (done) => {
      request(app)
        .delete('/location')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .set('lat',"99.9")
        .set('lng',"90.8")
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("delete location: location not exist", (done) => {
      request(app)
        .delete('/location')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .set('lat',"99.9")
        .set('lng',"90.8")
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put status: invalid user", (done) => {
      request(app)
        .put('/user/status')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .send({"status": true})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put status: invalid input", (done) => {
      request(app)
        .put('/user/status')
        .set('Authorization', "invalid")
        .set('Content-Type', 'application/json')
        .send({"status": true})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("add friend: successful", (done) => {
      request(app)
        .post('/friend/friend@gmail.com')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"name":'Billy Yan',"friendId":"444444"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put status: successful", (done) => {
      request(app)
        .put('/user/status')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"status": true})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put status: same status", (done) => {
      request(app)
        .put('/user/status')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"status": true})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put score: invalid user", (done) => {
      request(app)
        .put('/user/score')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .send({"score": 66})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put score: invalid input", (done) => {
      request(app)
        .put('/user/score')
        .set('Authorization', "invalid")
        .set('Content-Type', 'application/json')
        .send({"score": 66})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put score: successful", (done) => {
      request(app)
        .put('/user/score')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"score": 66})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put token: invalid user", (done) => {
      request(app)
        .put('/user/token')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .send({"token": 8979359})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put token: invalid input", (done) => {
      request(app)
        .put('/user/token')
        .set('Authorization', "invalid")
        .set('Content-Type', 'application/json')
        .send({"token": 8979359})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put token: successful", (done) => {
      request(app)
        .put('/user/token')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"token": 8979359})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post pn: invalid user", (done) => {
      request(app)
        .post('/pn')
        .set('Authorization', "abcdefg")
        .set('Content-Type', 'application/json')
        .send({"email": "friend@gmail.com"})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post pn: invalid input", (done) => {
      request(app)
        .post('/pn')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"email": "friendgmail.com"})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post pn: not friend", (done) => {
      request(app)
        .post('/pn')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"email": "friendsss@gmail.com"})
        .expect(405)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post pn: successful", (done) => {
      request(app)
        .post('/pn')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"email": "friend@gmail.com"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put status: successful", (done) => {
      request(app)
        .put('/user/status')
        .set('Authorization', "friend")
        .set('Content-Type', 'application/json')
        .send({"status": true})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post pn: friend busy", (done) => {
      request(app)
        .post('/pn')
        .set('Authorization', "qbcdefg")
        .set('Content-Type', 'application/json')
        .send({"email": "friend@gmail.com"})
        .expect(201)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put status: successful", (done) => {
      request(app)
        .put('/user/status')
        .set('Authorization', "friend")
        .set('Content-Type', 'application/json')
        .send({"status": false})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
  });