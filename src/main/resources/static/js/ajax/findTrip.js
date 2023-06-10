$(document).ready(function () {

    $("#sortTime").change(function () {
        // Lấy giá trị của lựa chọn đã chọn
        var sortTime = $(this).val();
        var sortPrice = $('#sortPrice').val();
        var vehicle = $('#vehicle').val();
        var startCity = $('#startCity').val();
        var endCity = $('#endCity').val();
        var startTime = $('#startTime').val();
        var endTime = $('#endTime').val();
        $.ajax({
            url: "/api/home/search/sort",
            type: "GET",
            data: {
                vehicle: vehicle,
                sortTime: sortTime,
                sortPrice: sortPrice,
                startCity: startCity,
                endCity: endCity,
                endTime: endTime,
                startTime: startTime,
            },
            dataType: "html",
            success: function (response) {
                $('#tbody').html(response);
                $.getScript("/js/ajax/home.js");
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
        var startCity = $('#startCity').val();
        var sortTime = $('#sortTime').val();
        var endCity = $('#endCity').val();
        var startTime = $('#startTime').val();
        var endTime = $('#endTime').val();
        $.ajax({
            url: "/api/home/search/sort",
            type: "GET",
            data: {
                vehicle: vehicle,
                sortTime: sortTime,
                sortPrice: sortPrice,
                startCity: startCity,
                endCity: endCity,
                endTime: endTime,
                startTime: startTime,
            },
            dataType: "html",
            success: function (response) {
                $('#tbody').html(response);
                $.getScript("/js/ajax/home.js");
                $.getScript("/js/ajax/findTrip.js");
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
        var startCity = $('#startCity').val();
        var sortTime = $('#sortTime').val();
        var endCity = $('#endCity').val();
        var startTime = $('#startTime').val();
        var endTime = $('#endTime').val();
        $.ajax({
            url: "/api/home/search/sort",
            type: "GET",
            data: {
                vehicle: vehicle,
                sortTime: sortTime,
                sortPrice: sortPrice,
                startCity: startCity,
                endCity: endCity,
                endTime: endTime,
                startTime: startTime,
            },
            dataType: "html",
            success: function (response) {
                $('#tbody').html(response);
                $.getScript("/js/ajax/home.js");
            },
            error: function () {
                console.log('Đã xảy ra lỗi khi gửi dữ liệu sort price Ajax.');
            }
        });


    });
});