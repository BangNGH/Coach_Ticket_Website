              function cancelForm() {
            window.location = "/home";
        }
            document.getElementById('bookButton').disabled = true;
            // Ẩn các cột "Chọn ghế" và "Sơ đồ ghế" ban đầu
            document.querySelectorAll(' th:nth-child(6), th:nth-child(7),td:nth-child(6), td:nth-child(7)').forEach(function(element) {
                element.style.display = 'none';
            });

            // Lắng nghe sự kiện khi người dùng chọn radio button "selectedTrip"
            document.querySelectorAll('input[name="selectedTrip"]').forEach(function(radio) {
                radio.addEventListener('click', function() {
                    // Ẩn các cột "Chọn ghế" và "Sơ đồ ghế" của các hàng khác
                    document.querySelectorAll('td:nth-child(6), td:nth-child(7)').forEach(function(element) {
                        // Xóa tất cả các checkbox đã chọn
                        document.querySelectorAll('input[name="selectedSeats"]:checked').forEach(function(checkbox) {
                            checkbox.checked = false;
                            checkbox.nextElementSibling.classList.remove('checked');
                        });
                        element.style.display = 'none';

                    });
                    // Hiển thị các cột "Chọn ghế" và "Sơ đồ ghế" của hàng tương ứng
                    this.closest('tr').querySelectorAll('td:nth-child(6), td:nth-child(7)').forEach(function(element) {
                        element.style.display = '';
                        document.getElementById('bookButton').disabled = false;
                    });
                    document.querySelectorAll(' th:nth-child(6), th:nth-child(7)').forEach(function(element) {
                        element.style.display = '';
                        document.getElementById('bookButton').disabled = false;
                    });
                });
            });

            document.getElementById('bookButton').addEventListener('click', function() {
                // Lấy giá trị của radio button "selectedTrip"
                var selectedTrip = document.querySelector('input[name="selectedTrip"]:checked');
                if (selectedTrip) {
                    // Lấy giá trị của ghế đã chọn
                    var selectedSeats = [];
                    var selectedSeatsElements = selectedTrip.closest('tr').querySelectorAll('input[name="selectedSeats"]:checked');
                    selectedSeatsElements.forEach(function(seatElement) {
                        selectedSeats.push(seatElement.value);
                    });

                    if (selectedSeats.length > 0) {
                        if (selectedSeats.length >5) {
                            alert('Chỉ đặt tối đa 5 vé cho 1 lần thanh toán!');
                            event.preventDefault();
                            return;
                        }

                        //Đặt vé

                        document.getElementById('selectedTripId').value = selectedTrip.value;
                        document.getElementById('inputSelectedSeats').value = selectedSeats.join(',');
                        document.getElementById('bookingForm').submit();
                        // Xóa bỏ các giá trị đã chọn trong mảng selectedSeats
                        selectedSeats.length = 0;
                    } else {
                        alert('Vui lòng chọn ít nhất một ghế.');
                        event.preventDefault();
                        return;
                    }
                } else {
                    alert('Vui lòng chọn chuyến đi.');
                    event.preventDefault();
                    return;
                }

            });
