use("User");
use("TodoList");
use("TodoListItem");
require("/inc/all.js");
require("/inc/models.js");
require("/inc/moment-with-langs.js");


var id;
var userId;
var name; 

var index = function() {
    var list=TodoList.getAll();
    if(!isObjectEmpty(list))
    for(var i=0;i<list.length;i++)
        print(JSON.stringify(list[i]));
};


var add = function() {
    userId = getSession('userId');
    if (isTextEmpty(userId)) {
        redirect("/login");
    }

    user = new User();
    user.id = userId;

    user = getFirstUser(user);
    if (!isObjectEmpty(user)) {
        if (isTextEmpty(name)) {
            var res = {
                success: false,
                error: "Name empty"
            };
            print(JSON.stringify(res));
            return;
        }

        var todoList = new TodoList();
        todoList.id = uniqueID();
        todoList.name = name;
        todoList.userId = userId;
        todoList.addDate = moment().format(DB_DATE_FORMAT);
        todoList.save();

        var res = {
            success: true,
            data: todoList
        };
        print(JSON.stringify(res));
    } else {
        var res = {
            success: false,
            error: "User not found"
        };
        print(JSON.stringify(res));
    }
};

var del = function() {
    userId = getSession('userId');
    if (isTextEmpty(userId)) {
        redirect("/login");
    }

    user = new User();
    user.id = userId;

    user = getFirstUser(user);
    if (!isObjectEmpty(user)) { 
        var todoList = new TodoList();
        todoList.id = id;
        todoList = getFirstTodoList(todoList); 
        var res = {};
        if (!isObjectEmpty(todoList)) { 
            todoList = new TodoList();
            todoList.id = id;
            TodoList.delete(todoList);
            
            var todoListItem=new TodoListItem();
            todoListItem.listId=id;
            var items=TodoListItem.search(todoListItem);
            if(!isObjectEmpty(items)){
              for(var i=0;i<items.length;i++){
                  todoListItem=new TodoListItem();
                  todoListItem.id=items[i].id;
                  TodoListItem.delete(todoListItem);
              }   
            }
            res = {success: true};
        } else {
            res = {
                success: false,
                error: "Todolist not found"
            };
        }
        print(JSON.stringify(res));
    } else {
        var res = {
            success: false,
            error: "User not found"
        };
        print(JSON.stringify(res));
    }
};

var edit = function() {
    userId = getSession('userId');
    if (isTextEmpty(userId)) {
        redirect("/login");
    }

    user = new User();
    user.id = userId;

    user = getFirstUser(user);
    if (!isObjectEmpty(user)) {
        if (isTextEmpty(name)) {
            var res = {
                success: false,
                error: "Name empty"
            };
            print(JSON.stringify(res));
            return;
        }
        var todoList = new TodoList();
        todoList.id = id;
        todoList = getFirstTodoList(todoList);

        var res = new Object();
        if (!isObjectEmpty(todoList)) {
            todoList.name = name;
            todoList.save();
            res.success = true;
        } else {
            res.success = false;
            res.error = "Todolist not found";
        }
        print(JSON.stringify(res));
    } else {
        var res = {
            success: false,
            error: "User not found"
        };
        print(JSON.stringify(res));
    }
};