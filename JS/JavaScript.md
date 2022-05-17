# JavaScript

### JQuery验证码60s倒计时

//点击发送验证码按钮触发下面函数
$("#sendCode").click(function () {
		//如果有disabled，说明最近已经点过，则什么都不做
		if($(this).hasClass("disabled")){

```javascript
	}else {
        //调用函数使得当前的文本进行倒计时功能
		timeOutChangeStyle();
		//发送验证码
		var phone=$("#phoneNum").val();
		$.get("/sms/sendCode?phone="+phone,function (data){
			if (data.code!=0){
				alert(data.msg);
			}
		})
	}
})

let time = 60;
function timeOutChangeStyle() {
	//开启倒计时后设置标志属性disable，使得该按钮不能再次被点击
	$("#sendCode").attr("class", "disabled");
    //当时间为0时，说明倒计时完成，则重置
	if(time==0){
		$("#sendCode").text("点击发送验证码");
		time=60;
		$("#sendCode").attr("class", "");
	}else {
        //每秒调用一次当前函数，使得time--
		$("#sendCode").text(time+"s后再次发送");
		time--;
		setTimeout("timeOutChangeStyle()", 1000);
	}
}
```