$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	// 获取要发送的请求参数，发送ajax请求
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName,"content":content},
		function (data) {
			data = $.parseJSON(data);
			if(data.code == 0){
				$("#hintBody").text("发送成功！");
			}else{
				$("#hintBody").text(data.msg);
			}

			// 提示框显示两秒后关闭，并重新加载页面
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 重载页面
				location.reload();
			}, 2000);
		}
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}