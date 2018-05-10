$(function () {
    var urlPath = $('#urlPath').val();

    tinymce.init({
        selector: "textarea#editable",
        plugins: [
            "advlist autolink autosave link image lists charmap print preview hr anchor pagebreak ",
            "searchreplace wordcount visualblocks visualchars code media nonbreaking ",
            "table contextmenu directionality emoticons template textcolor paste fullpage textcolor"
        ],
        branding: false, // tinymce的图标是否显示
        height: 400, // 编辑区域的高度
        menubar: false, // 此选项允许您指定哪些菜单应该出现以及它们出现在TinyMCE顶部的菜单栏中的顺序
        resize: true, // 允许垂直方向调整大小,false为禁止调整,both为水平与垂直方向
        // images_upload_url: urlPath + '/edit/upload',
        images_reuse_filename: false,
        // 此选项允许您用自定义逻辑替换TinyMCE的默认JavaScript上传处理程序函数
        images_upload_handler: function (blobInfo, success, failure) {
            var xhr = new XMLHttpRequest();
            xhr.withCredentials = false;
            xhr.open('POST', urlPath + '/edit/upload');
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
});