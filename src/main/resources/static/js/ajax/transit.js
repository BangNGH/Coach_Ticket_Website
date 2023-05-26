$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/transit/search',
            type: 'GET',
            data: {q: q},
            success: (data) => {

                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table
                    data.forEach((city) => {
                        const userRow = `
      <tr>

         <td>${city.booking.trip.route.name}</td>
                                <td>${city.booking.bookingDate}</td>
                                <td>${city.booking.trip.startTime}</td>
                                <td>${city.name}</td>
                                <td>${city.address}</td>
                                <td>${city.phone}</td>
        <td>
          <a href="/admin/transit/edit/${city.id}">Edit</a> |
          <a href="/admin/transit/delete/${city.id}">Delete</a>
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
                alert(q + " search fail " + error)
                console.log(error);
            }
        });
    });

});
