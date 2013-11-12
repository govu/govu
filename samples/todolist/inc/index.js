$(document).ready(function() {
    $("#btnAddList").click(function() {
        addList();
        return false;
    });
    $("#new_list").keypress(function(e) {
        if (e.which == 13) {
            addList();
            return false;
        }
    });
    init();
});

var init=function(){
    $(".todoListDel").unbind('click');
    $(".todoListDel").click(function() {
        if(confirm("Are you sure ?"))
            deleteList(this);
        return false;
    });
    $(".todoListEdit").unbind('click');
    $(".todoListEdit").click(function() {
        openEditList(this);
        return false;
    });
    
    $(".newlist_item_inpt").unbind('keypress');
    $(".newlist_item_inpt").keypress(function(e) {
        if (e.which == 13) {
            var id=$(this).attr("dataListId");
            addListItem(id);
            return false;
        } 
    });
    
    $(".btnAddListItem").unbind('click');
    $(".btnAddListItem").click(function() {
        var id=$(this).attr("dataListId");
        addListItem(id);
        return false;
    });
    
    $(".todoListItemDel").unbind('click');
    $(".todoListItemDel").click(function() {
        if(confirm("Are you sure ?"))
            deleteListItem(this);
        return false;
    });
    $(".todoListItemEdit").unbind('click');
    $(".todoListItemEdit").click(function() {
        openEditListItem(this);
        return false;
    });
}

var deleteList = function(item) {
    if (!isObjectEmpty(item)) {
        var listId = $(item).attr("dataId");
        if (!isTextEmpty(listId)) {
            $.ajax({
                type: "POST",
                dataType: 'json',
                url: "http://localhost/todoList/del",
                data: {'id': listId},
                error: function() {
                    console.log("delete totdolist error");
                },
                success: function(data) {
                    var dataJSON = null;
                    try {
                        if (typeof data == "string") {
                            dataJSON = $.parseJSON(data);
                        } else {
                            dataJSON = data;
                        }
                    } catch (e) {
                    }
                    if (dataJSON != null && dataJSON.success) {
                        $("#group_item_"+listId).remove();
                    }
                }
            }, "json");
        }
    }
}

var openEditList = function(item) {

}

var addList = function() {
    var name = $("#new_list").val();
    $("#new_list_div").removeClass("has-error");
    $("#new_list").val("");
    if (!isTextEmpty(name)) {
        $.ajax({
            type: "POST",
            dataType: 'json',
            url: "http://localhost/todoList/add",
            data: {'name': name},
            error: function() {
                console.log("create totdolist error");
            },
            success: function(data) {
                var dataJSON = null;
                try {
                    if (typeof data == "string") {
                        dataJSON = $.parseJSON(data);
                    } else {
                        dataJSON = data;
                    }
                } catch (e) {
                }
                if (dataJSON != null && dataJSON.success) {
                    data = dataJSON.data;
                    if (!isObjectEmpty(data)) {
                        var html = $('<div class="accordion-group" id='+data.id+'><div class="accordion-heading">'+
                                     '<a class="accordion-toggle collapsed todoListCss" data-toggle="collapse" data-parent="#todoList" href="#todoList_' + data.id + '">' + escapeHtml(data.name) + '</a>'+
                                     '<a class="btn btn-danger todoListDel" href="#" id="" dataId="'+data.id+'"><i class="icon-trash icon-white"></i></a>' +
                                     '<a class="btn btn-success todoListEdit" href="#" id="" dataId="'+data.id+'"><i class="icon-pencil icon-white"></i></a>' +
                                     '</div><div id="todoList_' + data.id + '" class="accordion-body collapse" style="height: 0px;"><div class="accordion-inner"></div></div></div>');
                        html.insertAfter($("#new_list_div"));
                        init();
                    }
                }
            }
        }, "json");
    } else {
        $("#new_list_div").addClass("has-error");
    }
}


var deleteListItem = function(item) {
    if (!isObjectEmpty(item)) {
        var listItemId = $(item).attr("dataId");
        if (!isTextEmpty(listItemId)) {
            $.ajax({
                type: "POST",
                dataType: 'json',
                url: "http://localhost/todoListItem/del",
                data: {'id': listItemId},
                error: function() {
                    console.log("delete totdolistitem error");
                },
                success: function(data) {
                    var dataJSON = null;
                    try {
                        if (typeof data == "string") {
                            dataJSON = $.parseJSON(data);
                        } else {
                            dataJSON = data;
                        }
                    } catch (e) {
                    }
                    if (dataJSON != null && dataJSON.success) {
                        $("#todoListItem_div_"+listItemId).remove();
                    }
                }
            }, "json");
        }
    }
}

var openEditListItem = function(item) {

}

var addListItem = function(listId) {
    var text = $("#new_list_item_"+listId).val();
    $("#new_list_item_"+listId).removeClass("has-error");
    $("#new_list_item_"+listId).val("");
    if (!isTextEmpty(text)) {
        $.ajax({
            type: "POST",
            dataType: 'json',
            url: "http://localhost/todoListItem/add",
            data: {'text': text,
                   'listId':listId},
            error: function() {
                console.log("create totdolistitem error");
            },
            success: function(data) {
                var dataJSON = null;
                try {
                    if (typeof data == "string") {
                        dataJSON = $.parseJSON(data);
                    } else {
                        dataJSON = data;
                    }
                } catch (e) {
                }
                if (dataJSON != null && dataJSON.success) {
                    data = dataJSON.data;
                    if (!isObjectEmpty(data)) {
                        var html = $('<div class="todoListItemCss" id="todoListItem_div_'+data.id+'">'+escapeHtml(data.text+' '+data.status)+'</pre>'+
                                     '<a class="btn btn-success todoListItemEdit" href="#" id="" dataId="'+data.id+'"><i class="icon-pencil icon-white"></i></a>'+
                                     '<a class="btn btn-danger todoListItemDel" href="#" id="" dataId="'+data.id+'"><i class="icon-trash icon-white"></i></a>'+ 
                                     '</div>');
                        html.insertAfter($("#new_list_item_div_"+data.listId));
                        init();
                    }
                }
            }
        }, "json");
    } else {
        $("#new_list_item_"+listId).addClass("has-error");
    }
}

