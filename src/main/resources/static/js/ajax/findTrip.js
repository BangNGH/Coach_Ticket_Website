$(document).ready(function() {
    $('#q').keyup(function() {
        // Lấy giá trị của ô input "q"
           var keyword = $(this).val().trim();
           var startCity = $('#startCity').val();
           var endCity = $('#endCity').val();
           var startTime = $('#startTime').val();
           var endTime = $('#endTime').val();
$.ajax({
    url: "/api/home/search",
    type: "GET",
    data: {
        startCity: startCity,
        endCity: endCity,
        endTime: endTime,
        startTime: startTime,
        keyword: keyword
    },
    dataType: "html",
    success: function(response) {
        $('#tbody').html(response);
         $.getScript("/js/ajax/home.js");
    },
    error: function() {
        alert("ERROR!" + keyword);
        console.log('Đã xảy ra lỗi khi gửi request Ajax.');
    }
});

    });
});