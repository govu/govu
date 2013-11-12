use("User");
require("/inc/all.js");
require("/inc/models.js");

var username;
var password;

var usernameError=false;
var passwordError=false;
var errorMessage;

var index = function() {
    var userId=getSession('userId');
    if(!isTextEmpty(userId)){
        redirect("/index");
    }
};

var doLogin = function() {
    if(isTextEmpty(username) || username.length<3){
        usernameError=true;
    }
    
    if(isTextEmpty(password) || password.length<4){
        passwordError=true;
    }
    
    if(!usernameError && !passwordError){
        var user=new User();
        user.userName=username.toLowerCase();
        user.password=password;
        var user=getFirstUser(user); 
        if(!isObjectEmpty(user)){ 
            setSession("userId",user.id);
            redirect("/index");
        }else{
            errorMessage="Login Failed Try Again!!!!";
        }
    }
    password=null;
};

