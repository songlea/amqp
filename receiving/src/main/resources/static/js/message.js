$(function () {
    var urlPath = $('#urlPath').val();

    var sock = new SockJS(urlPath + "/endpointMessage");
    var stomp = Stomp.over(sock);
    stomp.connect({}, function () {
        stomp.subscribe("/prefixMessage/getRabbitMQ", function (message) {
            if (message && message.body) {
                console.info(message.body);
                $('#messageFlag').before('<p>' + message.body + '</p>');
            }
        });
    });
});