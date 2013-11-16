use("Player");
var displayName;
var score;
var leaderBoard;
var index = function() {
    displayName = getCookie("displayName");
    leaderBoard = Player.getAll();
    leaderBoard.sort(function(p1, p2) {
        return p2.score - p1.score;
    });
};
var save = function() {
    var player = new Player();
    player.displayName = displayName;
    var existingPlayer = Player.get(player);
    if (existingPlayer!=null) { //Update score
        if (existingPlayer.score<parseInt(score)) {
            existingPlayer.score = parseInt(score);
            existingPlayer.save();
        }
    } else { //New user
        player.score = parseInt(score);
        player.save();
    }
    setCookie("displayName",displayName);
    redirect("/");
};