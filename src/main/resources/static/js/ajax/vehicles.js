$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/vehicles/search',
            type: 'GET',
            data: { q: q },
            success: (data) => {
                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table
                    data.forEach((vehicle) => {
                        const userRow = `
      <tr>
       <td>${vehicle.id}</td>
       <td>${vehicle.name}</td>
       <td>${vehicle.licensePlates}</td>
       <td>${vehicle.capacity}</td>
                         <td>
                    <img alt="seat-map" style="max-width: 100%; max-height: 170px;" src="${vehicle.routeImagePath}">
                  </td>
        <td>
          <a href="/admin/vehicles/edit/${vehicle.id}">Sửa</a> |
          <a href="/admin/vehicles/delete/${vehicle.id}" class="text-danger" onclick="return confirm('Bạn có chắc muốn xóa chứ?')">Xóa</a>
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
