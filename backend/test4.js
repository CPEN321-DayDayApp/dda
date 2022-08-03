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
    test("put gender: successful", (done) => {
      request(app)
        .put('/user/gender')
        .set('Authorization', "friend")
        .set('Content-Type', 'application/json')
        .send({"gender": "female"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put age: successful", (done) => {
      request(app)
        .put('/user/age')
        .set('Authorization', "friend")
        .set('Content-Type', 'application/json')
        .send({"age": 50})
        .expect(200)
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
    test("put score: successful", (done) => {
        request(app)
          .put('/user/score')
          .set('Authorization', "friend")
          .set('Content-Type', 'application/json')
          .send({"score": 100})
          .expect(200)
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
    
  });