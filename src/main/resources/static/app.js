var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/ws');// 로컬에서는  /ws로 해야 함
    let headers = {Authorization: $('#token').val()};
    stompClient = Stomp.over(socket);
    stompClient.connect(headers, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        
        
        stompClient.subscribe('/sub/room/1', function (greeting) {
			console.log(JSON.parse(greeting.body))
            showGreeting(JSON.parse(greeting.body).data.msg);
        },headers);
    });
}

function disconnect() {
	let headers = {Authorization: $('#token').val()};
    if (stompClient !== null) {
        stompClient.disconnect(function(){},headers);
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/pub/chat/1", {}, JSON.stringify({'msg': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function login (){
	var param = {userId:$("#id").val(),userPw:$("#pw").val()}
  $.ajax({
          type : "POST",
          url : "/user/auth/signin",
          contentType:'application/json',
　　		  data: JSON.stringify(param),        
          success : function(data, textStatus, xhr) {
             if (data.code != '') {
                  alert('로그인에 실패하였습니다.')
              } else {
                  $("#loginN").hide()
                  $("#loginY").show()
                  $("#loginName").text(data.data.id)
                  $("#token").val(data.data.accessToken)
              }
          },
          error : function(request, status, error) {
              alert("code:" + request.status + "\n" + "error:" + error);
          }

      })
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#loginAct" ).click(function() { login(); });
});