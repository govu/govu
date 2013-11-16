var frame = document.getElementById("canvas").getContext("2d");
var collision, width, height, nodeSize, dir, foodPos, score, snakeBody, gameLoop; //Game properties
var dirs = {37: 1, 38: 2, 39: 3, 40: 4}; //directions
function start() {
    frame.font = "14px Verdana";
    collision = false;
    width = height = 450;
    nodeSize = 10;
    dir = 3;
    snakeBody = [];
    for (var i = 4; i >= 0; i--) {snakeBody.push({x: i, y: 0});}
    foodPos = {x: Math.round(Math.random() * (width - nodeSize) / nodeSize), y: Math.round(Math.random() * (height - nodeSize) / nodeSize)};
    score = 0;
    if (!gameLoop) {document.addEventListener("keydown", function(e) {dir = (dirs[e.which] && Math.abs(dirs[e.which] - dir) != 2) ? dirs[e.which] : dir;}, false)}
    if (gameLoop) {clearInterval(gameLoop);}
    gameLoop = setInterval(update, 60);
}
function update() {
    frame.fillStyle = "white";
    frame.fillRect(0, 0, width, height + 30);
    frame.fillStyle = "black";
    frame.strokeRect(0, 0, width, height);
    var nx = snakeBody[0].x;
    var ny = snakeBody[0].y;
    nx += (dir == 3 ? 1 : dir == 1 ? -1 : 0);
    ny += (dir == 4 ? 1 : dir == 2 ? -1 : 0);
    for (var i = 0; i < snakeBody.length; i++) {
        if (snakeBody[i].x == nx && snakeBody[i].y == ny) {collision = true;}
    }
    if (collision || nx == -1 || nx == width / nodeSize || ny == -1 || ny == height / nodeSize) { //Check collision for borders
        document.getElementById("canvas").style.display="none";
        document.getElementById("score").value = score;
        document.getElementById("gameOver").style.display="block";
        return;
    }
    if (nx == foodPos.x && ny == foodPos.y) { //Eat Foot
        var tail = {x: nx, y: ny};
        score++;
        clearInterval(gameLoop);
        gameLoop = setInterval(update, Math.max(10, 60 - score * 5));
        foodPos = {x: Math.round(Math.random() * (width - nodeSize) / nodeSize), y: Math.round(Math.random() * (height - nodeSize) / nodeSize)};
    } else {
        var tail = snakeBody.pop();
        tail.x = nx;
        tail.y = ny;
    }
    snakeBody.unshift(tail);
    for (var i = 0; i < snakeBody.length; i++) {drawNode(snakeBody[i].x, snakeBody[i].y);}
    drawNode(foodPos.x, foodPos.y);
    drawNode(0, 46);
    frame.fillText(" x " + score, 10, height + 19);
}
function drawNode(x, y) {
    frame.fillStyle = "black";
    frame.fillRect(x * nodeSize, y * nodeSize, nodeSize, nodeSize);
}