//Use Album class which is located in /Model/Album.js by default
use("Album"); 

//Parameters set by submitted form at index.html
var id;
var artist;
var title;

//List of albums populated when index is called
var albums;



//Index function called on page load
var index = function() {
    albums = Album.getAll();
};

//Creates new album
var create = function() {
    //Create our new album
    var album = new Album();
    //Set parameters
    album.id = uniqueID(); 
    album.artist = artist;
    album.title = title;
    //Save album
    album.save();
    
    //Redirect to index function
    redirect("/");
};

//Removes album
var remove = function() {
    //Create album for reference to delete
    var album = new Album();
    //Set album id to delete
    album.id = id;
    //Delete album
    album.delete();
    
    //Redirect to index function
    redirect("/");
};