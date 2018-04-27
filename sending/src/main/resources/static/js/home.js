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

    // 发送消息
    $('#test').click(function () {
        $.ajax({
            dataType: "json",
            type: 'POST',
            url: urlPath + "/home/chat",
            data: {
                "message": "幽幽xxcccccccccccc"
            },
            success: function (data) {
                alert(data.message)
            }
        });
    });
});