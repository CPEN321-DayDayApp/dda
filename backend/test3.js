jest.mock('./Users')
const Friend = require("./friend");
const Users = require("./Users");
var db;
const user = new Users(db)
const friend = new Friend(user)
module.exports = () => describe('friend test', () => { 
    test("add friend: invalid user", () => {
       friend.addFriend("123456", "friend@gmail.com", "JY", "222222").then(data=>{
        expect(data).toBe(404);
       })
    });
    test("add friend: invalid friend", () => {
      friend.addFriend("111111", "friendsss@gmail.com", "JY", "222223").then(data=>{
       expect(data).toBe(404);
      })
   });
    test("add friend: invalid input", () => {
        friend.addFriend("111111", "friendgmail.com", "JY", "222222").catch(data=>{
         expect(data).toBe("Invalid Input");
        })
     });
     test("add friend: successful", () => {
        friend.addFriend("111111", "friend@gmail.com", "JY", "222222").then(data=>{
         expect(data).toBe(200);
        })
     });
     test("add friend: friend exist", () => {
        friend.addFriend("111111", "friend@gmail.com", "JY", "222222").then(data=>{
         expect(data).toBe(201);
        })
     });
     test("get friendlist: invalid user", () => {
        friend.getFriendList("123456").then(data=>{
         expect(data).toBe(404);
        })
     });
     test("get friendlist: invalid input", () => {
         friend.getFriendList("string").catch(data=>{
          expect(data).toBe("Invalid Input");
         })
      });
      test("get friendlist: successful", () => {
         friend.getFriendList("111111").then(data=>{
          expect(data[0].name).toBe("JY");
         })
      });
     test("get friend: invalid user", () => {
        friend.getFriend("123456","friend@gmail.com").then(data=>{
         expect(data).toBe(404);
        })
     });
     test("get friend: invalid input", () => {
         friend.getFriend("string","friendgmail.com").catch(data=>{
          expect(data).toBe("Invalid Input");
         })
      });
      test("get friend: invalid friend", () => {
         friend.getFriend("111111","friendsss@gmail.com").then(data=>{
          expect(data).toBe(404);
         })
      });
      test("get friend: successful", () => {
         friend.getFriend("111111","friend@gmail.com").then(data=>{
          expect(data.friendId).toBe("222222");
         })
      });
      test("delete friend: invalid user", () => {
        friend.deleteFriend("123456","friend@gmail.com").then(data=>{
         expect(data).toBe(404);
        })
     });
     test("delete friend: invalid input", () => {
         friend.deleteFriend("string","friendgmail.com").catch(data=>{
          expect(data).toBe("Invalid Input");
         })
      });
      test("delete friend: successful", () => {
         friend.deleteFriend("111111","friend@gmail.com").then(data=>{
          expect(data).toBe(2);
         })
      });
      test("delete friend: friend not exist", () => {
        friend.deleteFriend("111111","friend@gmail.com").then(data=>{
         expect(data).toBe(405);
        })
     });
     test("get friendlist: empty list", () => {
      friend.getFriendList("111111").then(data=>{
       expect(data).toBe(201);
      })
   });
})