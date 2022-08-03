const test1 = require('./test1');
const test2 = require('./test2')
const test3 = require('./test3')
describe('sequentially run tests', () => {
   test1();
   test2();
   test3();
})

