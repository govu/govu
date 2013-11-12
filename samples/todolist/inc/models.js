function getFirstUser (user){ 
    var result=new User();
    if(!isObjectEmpty(user)){ 
        var users=User.search(user); 
        if(!isObjectEmpty(users) && users.length>0){
            result=users[0];
        }else{
            result=null;
        }
    }
    return result;
}

function getFirstTodoList (todoList){ 
    var result=new TodoList();
    if(!isObjectEmpty(todoList)){ 
        var todoLists=TodoList.search(todoList);  
        if(!isObjectEmpty(todoLists) && todoLists.length>0){
            result=todoLists[0];
        }else{
            result=null;
        }
    }
    return result;
}

function getFirstTodoListItem (todoListItem){ 
    var result=new TodoListItem();
    if(!isObjectEmpty(todoListItem)){ 
        var todoListItems=TodoListItem.search(todoListItem);  
        if(!isObjectEmpty(todoListItems) && todoListItems.length>0){
            result=todoListItems[0];
        }else{
            result=null;
        }
    }
    return result;
}

