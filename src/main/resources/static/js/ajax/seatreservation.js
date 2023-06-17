$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/seat-reservation/search',
            type: 'GET',
            data: { q: q },
            success: (data) => {
                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table
                    data.forEach((seatReservation) => {
                        const booking_date = new Date(seatReservation.booking.bookingDate).toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric'});
                        const userRow = `
      <tr>
         <td>${seatReservation.id}</td>
                        <td>${seatReservation.booking.user.email}</td>
                        <td>${seatReservation.seat.vehicle.name}</td>
                        <td>${seatReservation.seat.name}</td>
                        <td>${seatReservation.seat.vehicle.licensePlates}</td>
                         <td>${seatReservation.booking.trip.startTime} ${booking_date} </td>
                        <td>${seatReservation.seatsAvailable}</td>
        <td>
          <a href="/admin/seat-reservation/edit/${seatReservation.id}">Sửa</a> 
         
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
