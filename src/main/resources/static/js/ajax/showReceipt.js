$(document).ready(function () {
    $('#q').keyup(function () {
        // Lấy giá trị của ô input "q"
        var keyword = $(this).val().trim();
        $.ajax({
            url: "/api/bookings/search-receipts",
            type: "GET",
            data: {
                keyword: keyword
            },
            dataType: "html",
            success: function (response) {
                $('#tbody').html(response);
                const script = document.createElement("script");
                script.src = "/js/ajax/showReceipt.js"; // Đường dẫn đến script
                document.body.appendChild(script);
            },
            error: function () {
                alert("ERROR!" + keyword);
                console.log('Đã xảy ra lỗi khi gửi request Ajax tìm kiếm hóa đơn.');
            }
        });

    });
});