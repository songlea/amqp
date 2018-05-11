$(function () {
    var urlPath = $('#urlPath').val();
    var $publishForm = $('#publishForm');

    tinymce.init({
        selector: "textarea#editable",
        plugins: [
            "advlist autolink autosave link image lists charmap print preview hr anchor pagebreak ",
            "searchreplace visualblocks visualchars code media nonbreaking ",
            "table contextmenu directionality emoticons template textcolor paste fullpage textcolor"
        ],
        branding: false, // tinymce图标是否显示
        height: 400, // 编辑区域的高度
        menubar: false, // 此选项允许您指定哪些菜单应该出现以及它们出现在TinyMCE顶部的菜单栏中的顺序
        resize: true, // 允许垂直方向调整大小,false为禁止调整,both为水平与垂直方向
        // images_upload_url: urlPath + '/blog/upload',
        images_reuse_filename: false,
        // 此选项允许您用自定义逻辑替换TinyMCE的默认JavaScript上传处理程序函数
        images_upload_handler: function (blobInfo, success, failure) {
            var xhr = new XMLHttpRequest();
            xhr.withCredentials = false;
            xhr.open('POST', urlPath + '/blog/upload');
            xhr.onload = function () {
                if (xhr.status !== 200) {
                    failure('请求失败: ' + xhr.status);
                    return;
                }
                var json = JSON.parse(xhr.responseText);
                if (!json || json.code !== 0) {
                    failure('上传失败: ' + xhr.responseText);
                    return;
                }
                // 返回的数据code=0
                success(json.data);
            };
            var formData = new FormData();
            formData.append('file', blobInfo.blob());
            xhr.send(formData);
        },
        toolbar1: "undo redo | cut copy paste | bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | table | hr removeformat | subscript superscript",
        toolbar2: "searchreplace | bullist numlist | outdent indent blockquote | link unlink anchor image media code | forecolor backcolor | charmap emoticons | preview print",
        toolbar3: "styleselect formatselect fontselect fontsizeselect | ltr rtl | visualchars visualblocks nonbreaking template pagebreak restoredraft",
        toolbar_items_size: 'small',
        style_formats: [
            {title: 'Bold text', inline: 'b'},
            {title: 'Red text', inline: 'span', styles: {color: '#ff0000'}},
            {title: 'Red header', block: 'h1', styles: {color: '#ff0000'}},
            {title: 'Example 1', inline: 'span', classes: 'example1'},
            {title: 'Example 2', inline: 'span', classes: 'example2'},
            {title: 'Table styles'},
            {title: 'Table row 1', selector: 'tr', classes: 'tablerow1'}
        ],
        templates: [
            {title: 'Test template 1', content: 'Test 1'},
            {title: 'Test template 2', content: 'Test 2'}
        ],
        language: 'zh_CN'
    });

    // 发布验证
    $publishForm.bootstrapValidator({
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        // 表单的各个字段验证
        fields: {
            blogTitle: {
                message: '文章标题验证失败',
                validators: {
                    notEmpty: {
                        message: '请输入文章标题！'
                    },
                    stringLength: {
                        max: 64,
                        message: '文章标题长度至多64位！'
                    }
                }
            }
        }
    });

    // 绑定form-pre-serialize事件,在触发form-serilaize事件前保存tinyMCE的数据到textarea中(重要)
    $publishForm.bind('form-pre-serialize', function() {
        tinymce.triggerSave();
    });

    // 发布按钮
    $('#publishBtn').click(function () {
        if ($publishForm.data('bootstrapValidator').isValid()) {
            // 手动校验tinyMCE的内容不为空
            var textBody = tinymce.activeEditor.getBody();
            if (textBody && textBody.innerHTML && textBody.innerHTML.indexOf('<img') === -1) { // 没有图片标签
                if ($.trim(textBody.textContent) === '') {
                    art.dialog({content: '请输入文章内容！', title: '提示', icon: 'face-sad', time: 2});
                    return false;
                }
            }
            $publishForm.ajaxSubmit({
                url: urlPath + "/blog/publish",
                type: "post",
                dataType: "json",
                timeout: 10000,
                success: function (data) {
                    if (data.code === 0) {
                        art.dialog({content: '发布文章成功！', title: '提示', icon: 'face-smile', time: 2});
                        // 重置表单
                        $publishForm[0].reset();
                        $publishForm.data('bootstrapValidator').resetForm(true);
                    } else {
                        art.dialog({content: data.message, title: '提示', icon: 'face-sad', time: 2});
                    }
                },
                error: function () {
                    art.dialog({content: '发布文章失败！', title: '提示', icon: 'face-sad', time: 2});
                }
            });
            return false;
        }
    });
});