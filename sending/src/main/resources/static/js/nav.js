// nav导航栏
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

    var $updatePasswordForm = $('#updatePasswordForm');
    // 修改密码验证
    loadUpdatePasswordValidate($updatePasswordForm);

    // 修改密码按钮
    $('#submitUpdatePassword').click(function () {
        if ($updatePasswordForm.data('bootstrapValidator').isValid()) {
            $updatePasswordForm.ajaxSubmit({
                url: urlPath + "/home/updatePassword",
                type: "post",
                dataType: "json",
                timeout: 10000,
                success: function (data) {
                    if (data.code === 0) {
                        // 隐藏模态框并重置表单
                        $('#updatePasswordModal').modal('hide');
                        $updatePasswordForm.data('bootstrapValidator').resetForm(true);
                        art.dialog({
                            content: '修改密码成功，请重新登录！', title: '提示', icon: 'face-smile', ok: function () {
                                window.location.href = urlPath + '/login/index';
                            }
                        });
                    } else {
                        art.dialog({content: data.message, title: '提示', icon: 'face-sad', time: 2});
                    }
                },
                error: function () {
                    art.dialog({content: '修改密码失败！', title: '提示', icon: 'face-sad', time: 2});
                }
            });
            return false;
        }
    });

    // 重置模态框验证
    $('#updatePasswordModal').on('hidden.bs.modal', function () {
        // 当modal隐藏时销毁验证再重新加载验证
        $updatePasswordForm.data('bootstrapValidator').destroy();
        $updatePasswordForm.data('bootstrapValidator', null);
        $updatePasswordForm[0].reset();
        loadUpdatePasswordValidate($updatePasswordForm);
    });

    // 修改密码验证
    function loadUpdatePasswordValidate(jqNode) {
        jqNode.bootstrapValidator({
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            // 表单的各个字段验证
            fields: {
                oldPassword: {
                    message: '旧密码验证失败',
                    validators: {
                        notEmpty: {
                            message: '请输入旧密码！'
                        }
                    }
                },
                newPassword: {
                    message: '新密码验证失败',
                    validators: {
                        notEmpty: {
                            message: '请输入新密码！'
                        },
                        stringLength: {
                            min: 6,
                            message: '密码长度应至少6位！'
                        }
                    }
                },
                repeatNewPassword: {
                    message: '重复新密码验证失败',
                    validators: {
                        notEmpty: {
                            message: '请再次输入新密码！'
                        },
                        // 与指定控件内容比较是否相同
                        identical: {
                            field: 'newPassword',
                            message: '两次输入的新密码不一致！'
                        }
                    }
                }
            }
        });
    }
});