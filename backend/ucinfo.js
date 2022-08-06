const {app,db,db2} = require('./app');
const request = require('supertest');
const https = require('https');
var token_author=0;
var token_victor=0;
var token_clara=0;
const post_data_author = "client_secret=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&grant_type=refresh_token&refresh_token=1%2F%2F041_pUJ-qR6XsCgYIARAAGAQSNwF-L9IrmDnFDGkyji2xnEAOpUqyt5VScgKFqwEHJ01mM6ISlAMao3m2J6yg93IUZEA0uEBeMMA&client_id=797282797623-4bbthablvage8mnothk3l442ef0jot01.apps.googleusercontent.com"
const post_data_clara = "client_secret=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&grant_type=refresh_token&refresh_token=1%2F%2F043UYPKXs7kiQCgYIARAAGAQSNwF-L9IrIDiSNrSn0_JFnNCIhJtCY6ZWT6G7uCgf5HRnMxBBonr8s_AMRcDumJ13FWTcExNv7HY&client_id=247799484266-0k161477epu2p1libkih6mqba90r8rvi.apps.googleusercontent.com"
const post_data_victor = "client_secret=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&grant_type=refresh_token&refresh_token=1%2F%2F04buwvJdqEnzHCgYIARAAGAQSNwF-L9IrbZ3p2qERHMfaAd7tI-W-S-IR1vVtVjRkpps5OflDqtZLHDc3pEB0AnocO25Z2DIatw4&client_id=663381427665-q7coo2fkjt3ts6bod46966cte69ha5j2.apps.googleusercontent.com"
function postCode(post_data) {
  // Build the post string from an object
  // An object of options to indicate where to post to
  var post_options = {
      host: 'oauth2.googleapis.com',
      path: '/token',
      method: 'POST',
      headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Content-Length': 279,
          'user-agent': 'google-oauth-playground'
      }
  };
  return new Promise((resolve,reject)=>{
    // Set up the request
  var post_req = https.request(post_options, function(res) {
    var data = '';
      res.setEncoding('utf8');
      res.on('data', function (chunk) {
        data += chunk.toString();
      });
      res.on('end', () => {
        data = JSON.parse(data);
        resolve(data)
      });
    });

    // post the data
    post_req.write(post_data);
    post_req.end();
  });

}

module.exports = () => describe('integration test', () => { 
  beforeAll(async () => {
    connection = await db.connected;
    connection2 = await db2.connected;
    await postCode(post_data_author).then(result=>{
      token_author=result['id_token']
    })
    await postCode(post_data_clara).then(result=>{
      token_clara=result['id_token']
    })
    await postCode(post_data_victor).then(result=>{
      token_victor=result['id_token']
    })
  });
    test("post user, add user", (done) => {
      request(app)
        .post('/user')
        .set('Authorization', token_author)
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
        .set('Authorization', token_author)
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
        .set('Authorization', token_clara)
        .set('Content-Type', 'application/json')
        .send({"token": "f6zMjW6A310:APA91bG7nrXVYj-nCgJrsRTuFE9kMg_vFLdOST77xAtjPeP0uFrZVOe-SP7wmU8i0s3_Y1W8SRCJaD3nxoIB7K0eJOiVTop5DV_f9h3rvdldngE2GpSNqwco98hSXyFAwqVDAYDeePKc"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put status: invalid user", (done) => {
      request(app)
        .put('/user/status')
        .set('Authorization', token_victor)
        .set('Content-Type', 'application/json')
        .send({"status": true})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    
    test("add friend: successful", (done) => {
      request(app)
        .post('/friend/zhuchunhao0330@gmail.com')
        .set('Authorization', token_author)
        .set('Content-Type', 'application/json')
        .send({"name":'Chunhao Zhu',"friendId":"105435138784111381037"})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put status: successful", (done) => {
      request(app)
        .put('/user/status')
        .set('Authorization', token_author)
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
        .set('Authorization', token_author)
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
        .set('Authorization', token_victor)
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
        .set('Authorization', token_author)
        .set('Content-Type', 'application/json')
        .send({"score": "jg"})
        .expect(400)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("put score: successful", (done) => {
      request(app)
        .put('/user/score')
        .set('Authorization', token_author)
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
        .set('Authorization', token_victor)
        .set('Content-Type', 'application/json')
        .send({"token": 8979359})
        .expect(404)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    
    test("put token: successful", (done) => {
      request(app)
        .put('/user/token')
        .set('Authorization', token_author)
        .set('Content-Type', 'application/json')
        .send({"token": 8979359})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    
  });