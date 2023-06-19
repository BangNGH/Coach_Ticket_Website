$(document).ready(function () {
    $('#q').keyup(function () {
        // Lấy giá trị của ô input "q"
        var keyword = $(this).val().trim();
        $.ajax({
            url: "/api/bookings/search-bill",
            type: "GET",
            data: {
                keyword: keyword
            },
            dataType: "html",
            success: function (response) {
                $('#tbody').html(response);
                $.getScript("/js/ajax/findBill.js");
            },
            error: function () {
                alert("ERROR!" + keyword);
                console.log('Đã xảy ra lỗi khi gửi request Ajax tìm kiếm hóa đơn.');
            }
        });

    });
    const currentDate = new Date();
    const bookingRows = document.querySelectorAll("#table1 tbody tr");
    bookingRows.forEach(function (row) {
        const bookingDateCell = row.querySelector("td:nth-child(3)");
        const bookingTimeCell = row.querySelector("td:nth-child(4)");
        const cancelButton = row.querySelector("td:last-child a:first-child");
        const paymentButton = row.querySelector("td:last-child a:last-child");

        const bookingDateStr = bookingDateCell.textContent;
        const bookingTimeStr = bookingTimeCell.textContent;
        const bookingDateParts = bookingDateStr.split("/");
        const bookingDate = new Date(
            bookingDateParts[2], // Năm
            bookingDateParts[1] - 1, // Tháng (giảm đi 1 vì tháng trong JavaScript bắt đầu từ 0)
            bookingDateParts[0] // Ngày
        );
        const bookingDateTimeStr = `${bookingDate.toLocaleDateString("en-US")} ${bookingTimeStr}`;
        const bookingDateTime = new Date(bookingDateTimeStr);
        if (bookingDateTime < currentDate) {
            cancelButton.style.display = "none";
            paymentButton.style.display = "none";

            const cancelledText = document.createElement("span");
            cancelledText.textContent = "Đã bị hủy";
            cancelledText.classList.add("text-danger");
            row.querySelector("td:last-child").appendChild(cancelledText);
        }
    });
});