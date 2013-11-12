use("User");
use("TodoList");
use("TodoListItem");
require("/inc/all.js");
require("/inc/models.js");
require("/inc/moment-with-langs.js");


var id;
var listId;
var text;
var status;


var index = function() {
    var list=TodoListItem.getAll();
    if(!isObjectEmpty(list))
    for(var i=0;i<list.length;i++)
        print(JSON.stringify(list[i]));
};


var add = function() {
    var userId = getSession('userId');
    if (isTextEmpty(userId)) {
        redirect("/login");
    }
    if (isTextEmpty(text)) {
        var res = {
            success: false,
            error: "Text empty"
        };
        print(JSON.stringify(res));
        return;
    }

    if (isTextEmpty(listId)) {
        var res = {
            success: false,
            error: "List id empty"
        };
        print(JSON.stringify(res));
        return;
    }

    var todoListItem = new TodoListItem();
    todoListItem.id = uniqueID();
    todoListItem.listId = listId;
    todoListItem.text = text;
    todoListItem.status = "open";
    todoListItem.addDate = moment().format(DB_DATE_FORMAT);
    todoListItem.lastUpdateDate = moment().format(DB_DATE_FORMAT);
    todoListItem.save();

    var res = {
        success: true,
        data: todoListItem
    };
    print(JSON.stringify(res));
};

var del = function() {
    var userId = getSession('userId');
    if (isTextEmpty(userId)) {
        redirect("/login");
    }

    var todoListItem = new TodoListItem();
    todoListItem.id = id;
    todoListItem = getFirstTodoListItem(todoListItem);

    var res = {};
    if (!isObjectEmpty(todoListItem)) {
        todoListItem = new TodoListItem();
        todoListItem.id = id;
        TodoListItem.delete(todoListItem);
        res = {success: true};
    } else {
        res = {
            success: false,
            error: "Todolistitem not found"
        };
    }
    print(JSON.stringify(res));
};

var edit = function() {
    var userId = getSession('userId');
    if (isTextEmpty(userId)) {
        redirect("/login");
    }
 
    if (isTextEmpty(text)) {
        var res = {
            success: false,
            error: "Text empty"
        };
        print(JSON.stringify(res));
        return;
    }
    
    if (isTextEmpty(status)) {
        var res = {
            success: false,
            error: "Status empty"
        };
        print(JSON.stringify(res));
        return;
    }
    
    
    var todoListItem = new TodoListItem();
    todoListItem.id = id;
    todoListItem = getFirstTodoListItem(todoListItem);

    var res = new Object();
    if (!isObjectEmpty(todoListItem)) {
        todoListItem.text = text;
        todoListItem.satus=status;
        todoListItem.lastUpdateDate = moment().format(DB_DATE_FORMAT);
        todoListItem.save();
        res.success = true;
    } else {
        res.success = false;
        res.error = "Todolistitem not found";
    }
    print(JSON.stringify(res));
};