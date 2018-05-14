/**
 * 登录界面处理js
 *
 * @author Song Lea
 */
var LOGIN = function () {
    // 界面DOM
    var urlPath = $('#urlPath').val();
    var $alertInfo = $('#alertInfo');
    var $tipInfo = $('#tipInfo');
    var $userName = $('#loginUsername');
    var $passWord = $('#loginPassword');
    var $code = $('#code');

    return {
        login: function () {
            var _user = $.trim($userName.val());
            if (_user === '') {
                $tipInfo.text("请输入用户名！");
                $alertInfo.show();
                return;
            }
            var _pass = $passWord.val();
            if (_pass === '') {
                $tipInfo.text("请输入密码！");
                $alertInfo.show();
                return;
            }
            var _code = $code.val();
            if (_code === '') {
                $tipInfo.text("请输入验证码！");
                $alertInfo.show();
                return;
            }
            $.ajax({
                dataType: "json",
                type: 'POST',
                url: urlPath + "/login/in",
                data: $("#loginForm").serialize(),
                success: function (data) {
                    if (data.code === 0) {
                        // 登录成功跳转到主页
                        location.href = urlPath + "/home/index";
                    } else if (data) {
                        $tipInfo.text(data.message);
                        $alertInfo.show();
                        // 验证码不正确时不重置整个表单,只是重置验证码输入框
                        if (data.message === '验证码不正确！') {
                            $code.val('');
                            $code.focus();
                        } else {
                            $("#loginForm")[0].reset();
                            $userName.focus();
                        }
                        // Math.random()使用不同参数值来强制查询后台数据
                        $('#codeImage')[0].src = urlPath + '/login/getIdentifyCode?date=' + Math.random();
                    }
                }
            });
        },
        unamecr: function (e) {
            if (e.which === 13) {
                $passWord.focus();
            }
        },
        ucodecr: function (e) {
            if (e.which === 13) {
                $("#loginBtn").click();
            }
        },
        upasscr: function (e) {
            if (e.which === 13) {
                $code.focus();
            }
        },
        reset: function () {
            $alertInfo.hide();
        }
    }
}();

$(function () {
    var urlPath = $('#urlPath').val();

    // 登录
    $("#loginBtn").click(LOGIN.login);
    $("#resetBtn").click(LOGIN.reset);
    $("input[name=loginUsername]").keypress(LOGIN.unamecr);
    $("input[name=loginPassword]").keypress(LOGIN.upasscr);
    $("input[name=code]").keypress(LOGIN.ucodecr);

    // 重置密码
    var $resetPasswordForm = $('#resetPasswordForm');
    loadResetPasswordValidate($resetPasswordForm);

    $('#submitResetPassword').click(function () {
        if ($resetPasswordForm.data('bootstrapValidator').isValid()) {
            $resetPasswordForm.ajaxSubmit({
                url: urlPath + "/login/resetPassword",
                type: "post",
                dataType: "json",
                timeout: 10000,
                success: function (data) {
                    if (data.code === 0) {
                        // 隐藏模态框并重置表单
                        $('#resetPasswordModal').modal('hide');
                        $resetPasswordForm.data('bootstrapValidator').resetForm(true);
                        artDialog.notice({content: '重置密码成功，请稍后查看邮箱！', title: '提示', icon: 'face-smile', time: 4});
                    } else {
                        artDialog.notice({content: data.message, title: '提示', icon: 'face-sad', time: 2});
                    }
                },
                error: function () {
                    artDialog.notice({content: '重置密码失败！', title: '提示', icon: 'face-sad', time: 2});
                }
            });
            return false;
        }
    });

    // 注册新账号
    var $registerForm = $('#registerForm');
    loadRegisterFormValidate($registerForm);

    // 注册用户按钮
    $('#submitRegister').click(function () {
        if ($registerForm.data('bootstrapValidator').isValid()) {
            $registerForm.ajaxSubmit({
                url: urlPath + "/login/register",
                type: "post",
                dataType: "json",
                timeout: 10000,
                success: function (data) {
                    if (data.code === 0) {
                        // 隐藏模态框并重置表单
                        $('#registerModal').modal('hide');
                        $registerForm.data('bootstrapValidator').resetForm(true);
                        artDialog.notice({content: '注册成功，请您登录！', title: '提示', icon: 'face-smile', time: 4});
                    } else {
                        artDialog.notice({content: data.message, title: '提示', icon: 'face-sad', time: 2});
                    }
                },
                error: function () {
                    artDialog.notice({content: '用户注册失败！', title: '提示', icon: 'face-sad', time: 2});
                }
            });
            return false;
        }
    });

    // 重置模态框验证
    $('#resetPasswordModal').on('hidden.bs.modal', function () {
        // 当modal隐藏时销毁验证再重新加载验证
        $resetPasswordForm.data('bootstrapValidator').destroy();
        $resetPasswordForm.data('bootstrapValidator', null);
        $resetPasswordForm[0].reset();
        loadResetPasswordValidate($resetPasswordForm);
    });
    $('#registerModal').on('hidden.bs.modal', function () {
        $registerForm.data('bootstrapValidator').destroy();
        $registerForm.data('bootstrapValidator', null);
        $registerForm[0].reset();
        loadRegisterFormValidate($registerForm);
    });

    // 重置密码
    function loadResetPasswordValidate(jqNode) {
        jqNode.bootstrapValidator({
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            // 表单的各个字段验证
            fields: {
                resetUsername: {
                    message: '注册用户名验证失败',
                    validators: {
                        notEmpty: {
                            message: '请输入注册用户名！'
                        }
                    }
                },
                resetEmail: {
                    message: '注册邮箱地址验证失败',
                    validators: {
                        notEmpty: {
                            message: '请输入注册邮箱地址！'
                        },
                        emailAddress: {
                            message: '邮箱地址格式不正确！'
                        }
                    }
                }
            }
        });
    }

    // 注册
    function loadRegisterFormValidate(jqNode) {
        jqNode.bootstrapValidator({
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            // 表单的各个字段验证
            fields: {
                username: {
                    message: '用户名验证失败',
                    validators: {
                        notEmpty: {
                            message: '请输入用户名！'
                        },
                        stringLength: {
                            max: 32,
                            message: '用户名长度至多32位！'
                        }
                    }
                },
                email: {
                    message: '邮箱地址验证失败',
                    validators: {
                        notEmpty: {
                            message: '请输入邮箱地址！'
                        },
                        emailAddress: {
                            message: '邮箱地址格式不正确！'
                        }
                    }
                },
                password: {
                    message: '密码验证失败',
                    validators: {
                        notEmpty: {
                            message: '请输入密码！'
                        },
                        stringLength: {
                            min: 6,
                            message: '密码长度应至少6位！'
                        }
                    }
                },
                repeatPassword: {
                    message: '重复密码验证失败',
                    validators: {
                        notEmpty: {
                            message: '请再次输入密码！'
                        },
                        identical: {
                            field: 'password',
                            message: '两次输入的密码不一致！'
                        }
                    }
                }
            }
        });
    }
});
