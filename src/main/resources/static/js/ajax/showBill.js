$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/bookings/search/bill',
            type: 'GET',
            data: {q: q},
            success: (data) => {
                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table
                    data.forEach((booking) => {
                        const booking_date = new Date(booking.bookingDate).toLocaleString('vi-VN', {
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric'
                        });
                        const price = new Intl.NumberFormat('vi-VN').format(booking.getBookingDetails[0].totalPrice);
                        const userRow = `
      <tr>
      <td>${booking.getBookingDetails[0].id.ticketCode}</td>
            <td>${booking.trip.route.name}</td>
            <td>${booking_date}</td>
            <td>${booking.trip.startTime}</td>
             <td>${booking.getBookingDetails[0].numberOfTickets}</td>
            <td>${price}</td>
        <td>
            <a href="/admin/delete/${booking.id}" class="text-danger">Hủy |</a>
                                <a href="/admin/send-email-reminder/${booking.id}">Nhắc nhở</a>
        </td>
      </tr>
    `;
                        usersTableBody.innerHTML += userRow;
                    });
                } else {
                    // If there are no search results, display a message
                    const noResultsRow = `
    <tr>
      <td colspan="8" class="text-center">Không tìm thấy kết quả.</td>
    </tr>
  `;
                    usersTableBody.innerHTML = noResultsRow;
                }

            },
            error: (error) => {
                alert(q + " search fail " + error)
                console.log(error);
            }
        });
    });

});
