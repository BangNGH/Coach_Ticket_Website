$(document).ready(function () {
    $("#sortTime").change(function () {
        // Lấy giá trị của lựa chọn đã chọn
        var sortTime = $(this).val();
        var sortPrice = $('#sortPrice').val();
        var vehicle = $('#vehicle').val();
        var endCity = $('#endCity').val();
        var endTime = $('#endTime').val();

        $.ajax({
            url: "/api/home/search-by-destination/sort",
            type: "GET",
            data: {
                vehicle: vehicle,
                sortTime: sortTime,
                sortPrice: sortPrice,
                endCity: endCity,
                endTime: endTime,
            },
            dataType: "html",
            success: function (response) {
                $('#tbody').html(response);
                $.getScript("/js/ajax/findDetisnation.js");
                $.getScript("/js/ajax/findByDestionation.js");
            },
            error: function (xhr, ajaxOptions, thrownError) {
                console.log(xhr.status);
                console.log(thrownError);
                console.log('Đã xảy ra lỗi khi gửi dữ liệu sort Ajax.');
                console.log(thrownError);
                console.log(xhr.responseText);
            }
        });
    });
    $("#sortPrice").change(function () {
        // Lấy giá trị của lựa chọn đã chọn
        var sortPrice = $(this).val();
        var vehicle = $('#vehicle').val();
        var sortTime = $('#sortTime').val();
        var endCity = $('#endCity').val();
        var endTime = $('#endTime').val();
        $.ajax({
            url: "/api/home/search-by-destination/sort",
            type: "GET",
            data: {
                vehicle: vehicle,
                sortTime: sortTime,
                sortPrice: sortPrice,
                endCity: endCity,
                endTime: endTime,
            },
            dataType: "html",
            success: function (response) {
                $('#tbody').html(response);
                $.getScript("/js/ajax/findDetisnation.js");
                $.getScript("/js/ajax/findByDestionation.js");
            },
            error: function () {
                console.log('Đã xảy ra lỗi khi gửi dữ liệu sort price Ajax.');
            }
        });


    });
    $("#vehicle").change(function () {
        // Lấy giá trị của lựa chọn đã chọn
        var vehicle = $(this).val();
        var sortPrice = $('#sortPrice').val();
        var sortTime = $('#sortTime').val();
        var endCity = $('#endCity').val();
        var endTime = $('#endTime').val();
        $.ajax({
            url: "/api/home/search-by-destination/sort",
            type: "GET",
            data: {
                vehicle: vehicle,
                sortTime: sortTime,
                sortPrice: sortPrice,
                endCity: endCity,
                endTime: endTime,
            },
            dataType: "html",
            success: function (response) {
                $('#tbody').html(response);
                $.getScript("/js/ajax/findDetisnation.js");
                $.getScript("/js/ajax/findByDestionation.js");
            },
            error: function () {
                console.log('Đã xảy ra lỗi khi gửi dữ liệu sort price Ajax.');
            }
        });
    });
});