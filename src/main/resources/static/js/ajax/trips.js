$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/trips/search',
            type: 'GET',
            data: { q: q },
            success: (data) => {
                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table
                    data.forEach((trip) => {
                        const price = new Intl.NumberFormat('vi-VN').format(trip.price);
                        const userRow = `
      <tr>
         <td>${trip.id}</td>
            <td>${trip.route.name}</td>
            <td>${trip.vehicle.name}</td>
             <td>${trip.vehicle.licensePlates}</td>
<td>${trip.startTime}</td>
           <td>${price}đ</td>
        <td>
          <a href="/admin/trips/edit/${trip.id}">Sửa</a> |
          <a href="/admin/trips/delete/${trip.id}" class="text-danger" onclick="return confirm('Bạn có chắc muốn xóa chứ?')">Xóa</a>
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
