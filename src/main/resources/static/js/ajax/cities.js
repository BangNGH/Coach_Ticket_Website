$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/cities/search',
            type: 'GET',
            data: { q: q },
            success: (data) => {

                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table
                    data.forEach((city) => {
                        const userRow = `
      <tr>
       <td>${city.id}</td>
       <td>${city.name}</td>
                              <td>
                            <img alt="hình tuyến đi" style="max-width: 100%; max-height: 170px;" src="${city.cityImagePath}">
                        </td>
        <td>
          <a href="/admin/cities/edit/${city.id}">Sửa</a> |
          <a href="/admin/cities/delete/${city.id}" class="text-danger" onclick="return confirm('Bạn có chắc muốn xóa chứ?')">Xóa</a>
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
