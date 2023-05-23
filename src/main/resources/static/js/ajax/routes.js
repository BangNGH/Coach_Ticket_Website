$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/routes/search',
            type: 'GET',
            data: { q: q },
            success: (data) => {
                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table
                    data.forEach((route) => {
                        const userRow = `
      <tr>
                       <td>${route.id}</td>
                        <td>${route.name}</td>
                        <td>${route.startCity.name}</td>
                        <td>${route.endCity.name}</td>
                        <td>${route.distance}km</td>
                        <td>${route.timeTrip}h</td>
                        <td>
                            <img alt="hình tuyến đi" style="max-width: 100%; max-height: 170px;" src="${route.routeImagePath}">
                        </td>
                        <td>
                            <a href="/admin/routes/edit/${route.id}">Edit</a> |
          <a href="/admin/routes/delete/${route.id}">Delete</a>
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
