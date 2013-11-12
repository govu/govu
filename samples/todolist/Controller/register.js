use("User");
require("/inc/all.js");
require("/inc/models.js");

var username;
var password;
var repassword;

var usernameError=false;
var passwordError=false; 
var errorMessage;

var index = function() {
    var userId=getSession('userId');
    if(!isTextEmpty(userId)){
        redirect("/index");
    }
};

var doRegister = function() {
    if(isTextEmpty(username) || username.length<3){
        usernameError=true;
    }
    
    if(isTextEmpty(password) || password.length<4){
        passwordError=true;
    }
    
    if(isTextEmpty(repassword) || repassword.length<4){
        passwordError=true;
    }else if(password!=repassword){
        passwordError=true;
    }
    
    if(!usernameError && !passwordError){
        var user=new User();
        user.userName=username.toLowerCase(); 
        var user=getFirstUser(user);
        if(!isObjectEmpty(user)){
            errorMessage="Username already taken!!!!";
        }else{
            user=new User();
            user.userName=username.toLowerCase(); 
            user.password=password; 
            user.id=uniqueID(); 
            user.save();
            setSession("userId",user.id);
            redirect("/index");
        }
    }
    password=null;
    repassword=null;
};

