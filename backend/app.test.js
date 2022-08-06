const test1 = require('./test1');
const uccompetition = require('./uccompetition')
const ucleaderboard = require('./ucleaderboard')
const uctodolist = require('./uctodolist')
const ucfriend = require('./ucfriend')
const uclocation = require('./uclocation')
const ucinfo = require('./ucinfo')
const ucpn = require('./ucpn')
describe('sequentially run tests', () => {
   test1();
   // test2();
   // test3();
   uctodolist();
   ucfriend();
   uclocation();
   ucinfo();
   ucpn();
   uccompetition();
   ucleaderboard();
})

