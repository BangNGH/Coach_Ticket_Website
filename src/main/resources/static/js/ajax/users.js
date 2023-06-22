$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/users/search',
            type: 'GET',
            data: { q: q },
            success: (data) => {
                const usersTableBody = document.querySelector('tbody');
                usersTableBody.innerHTML = ''; // Clear current table body

                if (data.length > 0) {
                    // Loop through the search results and add each user to the table
                    data.forEach((user) => {
                        const userRow = `
      <tr>
        <td>${user.id}</td>
        <td>${user.fullname}</td>
        <td>${user.email}</td>
        <td>${user.address}</td>
        <td>${user.role}</td>
         <td>
                <span class="badge bg-${user.isEnabled ? 'success' : 'danger'}">${user.isEnabled ? 'Đã kích hoạt' : 'Chưa kích hoạt'}</span>
            </td>
            <td>${user.provider != null ? user.provider : 'Tài khoản Travelista'}</td>
        <td>
          <a href="/admin/users/edit/${user.id}">${user.provider ? '' : 'Sửa |'}</a> 
          <a href="/admin/users/delete/${user.id}" class="text-danger" onclick="return confirm('Bạn có chắc muốn xóa chứ?')">Xóa</a>
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
