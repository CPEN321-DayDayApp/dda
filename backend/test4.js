const {app,db} = require('./app');
const request = require('supertest');
const https = require('https');
var token=0;
jest.setTimeout(10000)
function postCode() {
  // Build the post string from an object
  var post_data = "client_secret=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&grant_type=refresh_token&refresh_token=1%2F%2F041_pUJ-qR6XsCgYIARAAGAQSNwF-L9IrmDnFDGkyji2xnEAOpUqyt5VScgKFqwEHJ01mM6ISlAMao3m2J6yg93IUZEA0uEBeMMA&client_id=797282797623-4bbthablvage8mnothk3l442ef0jot01.apps.googleusercontent.com"
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
module.exports = () => describe('leaderboard test', () => { 
    beforeAll(async () => {
      connection = await db.connected;
      await postCode().then(result=>{
        token=result['id_token']
      })
    });
    test("post user, add user", (done) => {
      request(app)
        .post('/user')
        .set('Authorization', token)
        .set('Content-Type', 'application/json')
        .send({"token": 123321})
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    test("post competition", (done) => {
      request(app)
        .post('/competition')
        .set('Authorization', token)
        .set('Content-Type', 'application/json')
        .expect(200)
        .end(function(err,res){
          if(err) return done(err)
          return done();
        })
    });
    
  });