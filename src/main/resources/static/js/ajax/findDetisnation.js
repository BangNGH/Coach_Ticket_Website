$(document).ready(function () {

    // Ẩn các cột "Chọn ghế" và "Sơ đồ ghế" ban đầu
    document.querySelectorAll(' th:nth-child(7), th:nth-child(8), th:nth-child(9),td:nth-child(7), td:nth-child(8), td:nth-child(9)').forEach(function (element) {
        element.style.display = 'none';
    });
    let previousSelectedTrip = null;
    // Lắng nghe sự kiện khi người dùng chọn radio button "selectedTrip"
    document.querySelectorAll('input[name="selectedTrip"]').forEach(function (radio) {
        radio.addEventListener('click', function () {
            if (previousSelectedTrip === this) {
                // Bỏ chọn radio button hiện tại
                this.checked = false;
                previousSelectedTrip = null;

                // Ẩn các cột "Chọn ghế" và "Sơ đồ ghế" ban đầu
                document.querySelectorAll(' th:nth-child(7), th:nth-child(8), th:nth-child(9),td:nth-child(7), td:nth-child(8), td:nth-child(9)').forEach(function (element) {
                    element.style.display = 'none';
                });
            } else { // Ẩn các cột "Chọn ghế" và "Sơ đồ ghế" của các hàng khác
                document.querySelectorAll('td:nth-child(7), td:nth-child(8), td:nth-child(9)').forEach(function (element) {
                    // Xóa tất cả các checkbox đã chọn
                    document.querySelectorAll('input[name="selectedSeats"]:checked').forEach(function (checkbox) {
                        checkbox.checked = false;
                        checkbox.nextElementSibling.classList.remove('checked');
                    });
                    element.style.display = 'none';

                });
                var selectedTrip = document.querySelector('input[name="selectedTrip"]:checked');

                // Hiển thị các cột "Chọn ghế" và "Sơ đồ ghế" của hàng tương ứng
                this.closest('tr').querySelectorAll('td:nth-child(7), td:nth-child(8), td:nth-child(9)').forEach(function (element) {
                    element.style.display = '';
                });
                document.querySelectorAll(' th:nth-child(7), th:nth-child(8), th:nth-child(9)').forEach(function (element) {
                    element.style.display = '';
                });
                previousSelectedTrip = this;
            }
            document.getElementById('bookButton_' + selectedTrip.value).addEventListener('click', function () {
                // Lấy giá trị của radio button "selectedTrip"
                var selectedTrip = document.querySelector('input[name="selectedTrip"]:checked');
                if (selectedTrip) {
                    // Lấy giá trị của ghế đã chọn
                    var selectedSeats = [];
                    var selectedSeatsElements = selectedTrip.closest('tr').querySelectorAll('input[name="selectedSeats"]:checked');
                    selectedSeatsElements.forEach(function (seatElement) {
                        selectedSeats.push(seatElement.value);
                    });

                    if (selectedSeats.length > 0) {
                        if (selectedSeats.length > 5) {
                            alert('Chỉ đặt tối đa 5 vé cho 1 lần thanh toán!');
                            event.preventDefault();
                            return;
                        }

                        //Đặt vé
                        document.getElementById('selectedTripId').value = selectedTrip.value;
                        document.getElementById('inputSelectedSeats').value = selectedSeats.join(',');
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
        });
    });

    function scrollToChosenRow() {
        const chosenRow = document.getElementById("choosenrow");
        if (chosenRow) {
            chosenRow.scrollIntoView({behavior: "smooth"});
        }
    }

    const radioButtons = document.querySelectorAll('input[type="radio"][name="selectedTrip"]');
    radioButtons.forEach(function (radio) {
        radio.addEventListener("change", scrollToChosenRow);
    });
});
