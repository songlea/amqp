$(function () {
    // 请求的根地址,包含ContentPath
    var urlPath = $('#urlPath').val();

    // 请求完成时根据返回来判断是否要重新登录
    $(document).ajaxComplete(function (event, xhr) {
        if (xhr.responseText && xhr.responseText.indexOf('No Login') !== -1) {
            art.dialog({content: '登录过期，请重新登录！', title: '提示', icon: 'face-sad', time: 2});
            setTimeout(function () {
                window.location.href = urlPath + '/login/index';
            }, 3000);
            return false;
        }
    });

    // 退出系统按钮
    $('#logoutBtn').click(function () {
        art.dialog({
            title: '提示',
            content: '您确定要退出吗？',
            icon: 'question',
            lock: true,
            opacity: 0.6,
            ok: function () {
                window.location.href = urlPath + '/login/out';
            },
            cancel: true
        });
    });

    var $sendMessageForm = $('#sendMessageForm');
    // 发送消息
    $sendMessageForm.bootstrapValidator({
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        // 表单的各个字段验证
        fields: {
            message: {
                message: '消息内容验证失败',
                validators: {
                    notEmpty: {
                        message: '消息内容不能为空'
                    }
                }
            }
        }
    });

    // 用jquery.form提交并用bootstrapValidator验证输入
    $('#sendMessageBth').click(function () {
        if ($sendMessageForm.data('bootstrapValidator').isValid()) {
            // ajaxForm不能提交表单，在document的ready函数中，使用ajaxForm来为AJAX提交表单进行准备。提交动作必须由submit开始；
            // ajaxSubmit 马上由AJAX来提交表单，可以在任何情况下进行该项提交。
            $sendMessageForm.ajaxSubmit({
                url: urlPath + "/home/chat",
                type: "post",
                dataType: "json",
                timeout: 10000,
                success: function (data) {
                    if (data.code === 0) {
                        // 样式:无规则字符串也能自动换行
                        var str = '<div class="input-group" style="word-wrap:break-word;word-break:break-all;">' +
                            '<span class="glyphicon glyphicon-user"></span>&nbsp;&nbsp;<span>' + data.data + '</span>' +
                            '<div class="well well-sm">' + $('#message').val() + '</div></div>';
                        $sendMessageForm.before(str);
                        // 请求成功后必须重置表单验证
                        $sendMessageForm.data('bootstrapValidator').resetForm(true);
                    } else {
                        art.dialog({content: data.message, title: '提示', icon: 'face-sad', time: 2});
                    }
                },
                error: function () {
                    art.dialog({content: '消息发送失败！', title: '提示', icon: 'face-sad', time: 2});
                }
            });
            //阻止表单默认提交
            return false;
        }
    });
});