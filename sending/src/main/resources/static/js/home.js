$(function () {
    // 请求的根地址,包含ContentPath
    var urlPath = $('#urlPath').val();

    var $sendMessageForm = $('#sendMessageForm');
    // 发送消息验证
    loadSendMessageValidate($sendMessageForm);

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
            // 阻止表单默认提交
            return false;
        }
    });

    // 发送消息验证
    function loadSendMessageValidate(jqNode) {
        jqNode.bootstrapValidator({
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
    }

    // 加载文章列表
    $.ajax({
        url: urlPath + "/blog/articles",
        type: "get",
        dataType: "json",
        timeout: 10000,
        data: {
            start: 0,
            limit: 20
        },
        success: function (content) {
            if (content && content.code === 0) {
                var data = content.data, _html = '';
                for (var i = 0, length = data.length; i < length; i++) {
                    _html += "<div class='page-header'>" +
                        "<h4>" + data[i].title + "  <small>" + data[i].createTime + "</small></h4>" +
                        "<p>作者：" + data[i].userName + "</p>" +
                        "<p><a class='btn btn-link' target='_blank' href='" + urlPath + "/articles/" + data[i].id + "' role='button'>&gt;&gt;&gt;查看详情</a></p>" +
                        "</div>";
                }
            }
            $("#mainContent").append(_html);
        },
        error: function () {
            art.dialog({content: '加载文章列表失败！', title: '提示', icon: 'face-sad', time: 2});
        }
    });
});