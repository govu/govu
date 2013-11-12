var DB_DATE_FORMAT = "YYYYMMddHHmmss";
var entityMap = {
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': '&quot;',
    "'": '&#39;',
    "/": '&#x2F;'
};

function escapeHtml(string) {
    return String(string).replace(/[&<>"'\/]/g, function(s) {
        return entityMap[s];
    });
}
function isTextEmpty(text) {
    return isObjectEmpty(text) || text == "";
}
 

function isObjectEmpty(obj) {
    return typeof obj == "undefined" || obj == null;
}

function showAlert (message) {
    if (!isTextEmpty(message)) {
        var msg = $('<div class="alert alert-danger">' + message + '</div>');
        if ($(".alert_div"))
            $(".alert_div").append(msg);
        else
            $(document).append(msg);
        setTimeout(function() {
            msg.remove();
        }, 3000);
    }
}

function compareToAddDate(a, b) {
    if (a.addDate > b.addDate)
        return -1;
    if (a.addDate < b.addDate)
        return 1;
    return 0;
}

function compareToAddlastUpdateDate(a, b) {
    if (a.lastUpdateDate > b.lastUpdateDate)
        return -1;
    if (a.lastUpdateDate < b.lastUpdateDate)
        return 1;
    return 0;
}
