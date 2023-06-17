$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/booking-details/search',
            type: 'GET',
            data: { q: q },
            success: (data) => {
                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table

                    data.forEach((booking) => {
                        const price = new Intl.NumberFormat('vi-VN').format(booking.totalPrice);
                        const userRow = `
      <tr>
                   <td>${booking.id.ticketCode}</td>
                   <td>${booking.id.bookingId}</td>
                        <td>${booking.numberOfTickets}</td>
                        <td>${price}</td>
        <td>
          <a href="/admin/booking-details/edit/${booking.id.bookingId}/${booking.id.ticketCode}">Sửa</a> |
          <a href="/admin/booking-details/edit/${booking.id.bookingId}/${booking.id.ticketCode}" class="text-danger" onclick="return confirm('Bạn có chắc muốn xóa chứ?')">Xóa</a>
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
                alert(q+" search fail "+error)
                console.log(error);
            }
        });
    });

});
