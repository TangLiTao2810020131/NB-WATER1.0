<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title></title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${ctx}/resources/plugins/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${ctx}/resources/css/admin.css" media="all">
    <link rel="stylesheet" href="${ctx}/resources/plugins/zTree_v3/css/zTreeStyle/zTreeStyle.css" type="text/css">
    <link rel="stylesheet" href="${ctx}/resources/css/addClass.css" media="all"/>
</head>
<body>
<div class="layui-body layui-bg-white">
    <div class="layui-fluid">
        <div class="layui-row  layui-col-space15">
            <fieldset class="layui-elem-field layui-field-title"
                      style="margin-top: 12px;">
                <legend>账号信息</legend>
            </fieldset>
            <form class="layui-form" action="" method="post">
                <input type="hidden" name="id"  value="${user.id }"/>
                <div class="layui-form-item">
                    <label class="layui-form-label">用户名:</label>
                    <label class="layui-form-mid">${user.username}</label>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">邮箱:</label>
                    <div class="layui-input-block">
                        <input type="text" name="email" id="email" value="${user.email}"
                               lay-verify="email" autocomplete="off" placeholder="请输入邮箱"
                               class="layui-input">
                    </div>
                </div>

                <div class="layui-form-item">
                    <label class="layui-form-label">手机:</label>
                    <div class="layui-input-block">
                        <input type="text" name="tel" id="tel" value="${user.tel}"
                               lay-verify="required|phone" autocomplete="off" placeholder="请输入手机"
                               class="layui-input">
                    </div>
                </div>
                <!--
					<div class="layui-form-item">
						<label class="layui-form-label">注册时间:</label> <label
							class="layui-form-mid"><fmt:formatDate
								value="${user.ctime }" pattern="yyyy-MM-dd	HH:mm:ss" /></label>
					</div>
					 -->


                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn" lay-submit="" lay-filter="demo1" id="demo1">确认修改</button>
                    </div>
                </div>

            </form>
        </div>
    </div>
</div>

<script src="${ctx}/resources/js/jquery-1.11.2.min.js"></script>
<script src="${ctx}/resources/plugins/layui/layui.js"></script>
<script src="${ctx}/resources/js/formatTime.js"></script>

<script>
    layui.use('element', function () {
        var element = layui.element;
    });

    layui.use(['form', 'layedit', 'laydate'], function () {
        var form = layui.form
            , layer = layui.layer
            , layedit = layui.layedit
            , laydate = layui.laydate;
        //自定义验证规则
        form.verify({
            phone: [/^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$/, '手机请输入正确的格式！']
            ,email: [/^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/, '邮箱格式不正确']
        });



        //监听提交
        form.on('submit(demo1)', function (data) {
            var data = $("form").serialize();
            console.log(data);
            $.ajax({
                type : "POST", //提交方式
                url : "${ctx}/user/edit.action", //路径
                data : data, //数据，这里使用的是Json格式进行传输
                dataType : "json",
                async : false,
                success : function(result) { //返回数据根据结果进行相应的处理
                    console.log(result);
                    var status = result.status;
                    var msg = result.msg;
                    layer.msg(msg, {
                        icon : 1,
                        time : 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function() {



                        layer.close(layer.index);
                        window.parent.location.reload();
                    });
                },
                error : function(result) {
                    layer.msg("操作失败!", {
                        icon : 1,
                        time : 2000 //2秒关闭（如果不配置，默认是3秒）
                    }, function() {
                        layer.close(layer.index);
                        window.parent.location.reload();
                    });
                }
            });
            return false;
        });
    })
</script>
</body>
</html>