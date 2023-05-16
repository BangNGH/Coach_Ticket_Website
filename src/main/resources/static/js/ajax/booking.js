$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/bookings/search',
            type: 'GET',
            data: { q: q },
            success: (data) => {
                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table
                    data.forEach((booking) => {
                        const booking_date = new Date(booking.booking_date).toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric'});
                        const userRow = `
      <tr>
       <td>${booking.id}</td>
            <td>${booking.trip.route.name}</td>
            <td>${booking.user.email}</td>
            <td>${booking_date}</td>
            <td>${booking.trip.startTime}</td>
        <td>
          <a href="/admin/bookings/edit/${booking.id}">Edit</a> |
          <a href="/admin/bookings/delete/${booking.id}">Delete</a>
        </td>
      </tr>
    `;
                        usersTableBody.innerHTML += userRow;
                    });
                } else {
                    // If there are no search results, display a message
                    const noResultsRow = `
    <tr>
      <td colspan="8" class="text-center">No results found</td>
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