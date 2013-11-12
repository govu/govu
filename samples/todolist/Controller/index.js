use("User"); 
use("TodoList"); 
use("TodoListItem"); 
require("/inc/all.js");
require("/inc/models.js");

var user;
var lists;

var index = function() {
    
    var userId=getSession('userId');
    if(isTextEmpty(userId)){
        redirect("/login");
    }
    
    user=new User();
    user.id=userId;
    
    user=getFirstUser(user);
    if(!isObjectEmpty(user)){ 
        
        var todoList=new TodoList();
        var todoListItem=new TodoListItem();
        todoList.userId=user.id;
        
        user.list= TodoList.search(todoList);
        if(!isObjectEmpty(user.list)){
            for(var i=0;i<user.list.length;i++){
                todoList=user.list[i];
                todoListItem=new TodoListItem();
                todoListItem.listId=todoList.id;
                todoList.items=TodoListItem.search(todoListItem);
                if(isObjectEmpty(todoList.items))
                    todoList.items=[];
                todoList.items.sort(compareToAddlastUpdateDate);
            }
            user.list.sort(compareToAddDate);
        }else{
            user.list=[];
        }
    }else{
        redirect("/logout");
    }
};

