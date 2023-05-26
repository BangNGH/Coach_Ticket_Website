$(document).ready(function () {
    const searchInput = document.querySelector('#q');
    searchInput.addEventListener('keyup', (event) => {
        const q = event.target.value.trim();
        $.ajax({
            url: '/api/contacts/search',
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
       <td>${city.email}</td>
       <td>${city.name}</td>
       <td>${city.title}</td>
       <td>${city.content}</td>
      
        <td>
          <a href="/admin/contacts/delete/${city.id}">Delete</a>
        </td>
      </tr>
    `;
                        usersTableBody.innerHTML += userRow;
                    });
                } else {
                    // If there are no search results, display a message
                    const noResultsRow = `
    <tr>
      <td colspan="8" class="text-center">Không tìm thấy kết quả</td>
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
