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
                    const price = new Intl.NumberFormat('vi-VN').format(bookingDetail.totalPrice);
                    data.forEach((booking) => {

                        const userRow = `
      <tr>
                   <td>${bookingDetail.id}</td>
                        <td>${bookingDetail.booking.id}</td>
                        <td>${bookingDetail.numberOfTickets}</td>
                        <td>${price}</td>
        <td>
          <a href="/admin/booking-details/edit/${booking.id}">Edit</a> |
          <a href="/admin/booking-details/delete/${booking.id}">Delete</a>
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
