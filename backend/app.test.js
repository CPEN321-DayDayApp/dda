const {app} = require('./app');
const request = require('supertest');

function MockPayLoad(){
  this.getPayload=function(){
    return {"sub":222222,"email":"mock@gmail.com","name":"fake name"};
  }
}
function MockOAuth2Client(){
  this.verifyIdToken=async function(){
    return new MockPayLoad();
  }
}

jest.mock("google-auth-library", ()=>{
  const res = {
    __esModule: true,
    OAuth2Client: jest.fn(()=>{return new MockOAuth2Client();}),
  }
  return res
});
jest.mock('./todoList');
describe('test TDL', () => { 
  test("should add task", (done) => {
    request(app)
      .post('/tdl')
      .set('Authorization', "qbcdefg")
      .set('Content-Type', 'application/json')
      .send({"taskId":111111,"lat":99.9,"lng":88.8,"task":"BBQ","time":30,"date":"Jan 11st"})
      .expect(200)
      .end(function(err,res){
        if(err) return done(err);
        return done();
      })
  });
  test("should get task", (done) => {
    request(app)
      .get('/tdl')
      .set('Authorization', "qbcdefg")
      .set('Content-Type', 'application/json')
      .expect(200)
      .end(function(err,res){
        if(err) return done(err);
        return done();
      })
  });
  test("should edit task", (done) => {
    request(app)
      .put('/tdl/111111')
      .set('Authorization', "qbcdefg")
      .set('Content-Type', 'application/json')
      .send({"lat":99.9,"lng":88.8,"task":"BBQ","time":30,"date":"Jan 11st"})
      .expect(200)
      .end(function(err,res){
        if(err) return done(err);
        return done();
      })
  });
  test("should delete task", (done) => {
    request(app)
      .delete('/tdl/111111')
      .set('Authorization', "qbcdefg")
      .set('Content-Type', 'application/json')
      .expect(200)
      .end(function(err,res){
        if(err) return done(err);
        return done();
      })
  });
});

