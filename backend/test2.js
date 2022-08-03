jest.mock('./Users')
const TodoList = require("./todoList");
const Users = require("./Users");
const user = new Users()
const tdl = new TodoList(user)
module.exports = () => describe('todolist test', () => { 
   test("get task: empty tdl", () => {
      tdl.getTDL("111111").then(data=>{
       expect(data).toBe(201);
      })
   });
    test("add task: invalid user", () => {
       tdl.addTDL("123456",654321,99.8,87.6,"Debug",50,"June 6th").then(data=>{
        expect(data).toBe(404);
       })
    });
    test("add task: invalid input", () => {
        tdl.addTDL("123456","string",99.8,87.6,"Debug",50,"June 6th").catch(err=>{
         expect(err).toBe("Invalid Input");
        })
     });
     test("add task: successful", () => {
        tdl.addTDL("111111",654321,99.8,87.6,"Debug",50,"June 6th").then(data=>{
         expect(data).toBe(200);
        })
     });
     test("add task: task exist", () => {
        tdl.addTDL("111111",654321,99.8,87.6,"Debug",50,"June 6th").then(data=>{
         expect(data).toBe(405);
        })
     });
     test("get task: invalid user", () => {
        tdl.getTDL("123456").then(data=>{
         expect(data).toBe(404);
        })
     });
     test("get task: invalid input", () => {
        tdl.getTDL("string").catch(data=>{
         expect(data).toBe("Invalid Input");
        })
     });
     test("get task: successful", () => {
        tdl.getTDL("111111").then(data=>{
         expect(data[0]).toBe(654321);
        })
     });
     test("edit task: invalid user", () => {
        tdl.editTDL("123456",654321,99.8,87.6,"Debug",50,"June 6th").then(data=>{
         expect(data).toBe(404);
        })
     });
     test("edit task: invalid input", () => {
        tdl.editTDL("111111","string",99.8,87.6,"Debug",50,"June 6th").catch(data=>{
         expect(data).toBe("Invalid Input");
        })
     });
     test("edit task: successful", () => {
        tdl.editTDL("111111",654321,99.8,87.6,"Debug",50,"June 6th").then(data=>{
         expect(data).toBe(200);
        })
     });
     test("delete task: invalid user", () => {
        tdl.deleteTDL("123456",654321).then(data=>{
         expect(data).toBe(404);
        })
     });
     test("delete task: invalid input", () => {
        tdl.deleteTDL("111111","string").catch(data=>{
         expect(data).toBe("Invalid Input");
        })
     });
     test("delete task: successful", () => {
        tdl.deleteTDL("111111",654321).then(data=>{
         expect(data).toBe(200);
        })
     });
     test("delete task: task not exist", () => {
        tdl.deleteTDL("111111",654321).then(data=>{
         expect(data).toBe(405);
        })
     });
     test("edit task: task not exist", () => {
        tdl.editTDL("111111",654321,99.8,87.6,"Debug",50,"June 6th").then(data=>{
         expect(data).toBe(405);
        })
     });
     
})